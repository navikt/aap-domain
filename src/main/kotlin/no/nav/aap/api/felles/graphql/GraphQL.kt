package no.nav.aap.api.felles.graphql

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import java.util.function.Predicate
import org.slf4j.LoggerFactory
import org.springframework.graphql.ResponseError
import org.springframework.graphql.client.ClientGraphQlRequest
import org.springframework.graphql.client.FieldAccessException
import org.springframework.graphql.client.GraphQlClient
import org.springframework.graphql.client.GraphQlClientInterceptor
import org.springframework.graphql.client.GraphQlClientInterceptor.Chain
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.reactive.function.client.WebClient
import reactor.util.retry.Retry.RetrySignal
import no.nav.aap.api.felles.error.IrrecoverableGraphQLException.BadGraphQLException
import no.nav.aap.api.felles.error.IrrecoverableGraphQLException.NotFoundGraphQLException
import no.nav.aap.api.felles.error.IrrecoverableGraphQLException.UnauthenticatedGraphQLException
import no.nav.aap.api.felles.error.IrrecoverableGraphQLException.UnauthorizedGraphQLException
import no.nav.aap.api.felles.error.IrrecoverableGraphQLException.UnexpectedResponseGraphQLException
import no.nav.aap.api.felles.error.RecoverableGraphQLException
import no.nav.aap.api.felles.error.RecoverableGraphQLException.UnhandledGraphQLException
import no.nav.aap.api.felles.error.RecoverableIntegrationException
import no.nav.aap.api.felles.graphql.GraphQLErrorHandler.Companion.BadRequest
import no.nav.aap.api.felles.graphql.GraphQLErrorHandler.Companion.NotFound
import no.nav.aap.api.felles.graphql.GraphQLErrorHandler.Companion.Unauthenticated
import no.nav.aap.api.felles.graphql.GraphQLErrorHandler.Companion.Unauthorized
import no.nav.aap.api.felles.graphql.GraphQLExtensions.oversett
import no.nav.aap.rest.AbstractRestConfig
import no.nav.aap.rest.AbstractWebClientAdapter
import no.nav.aap.util.LoggerUtil

interface GraphQLErrorHandler {

    fun handle(e : Throwable) : Nothing

    companion object {

        const val Ok = "ok"
        const val Unauthorized = "unauthorized"
        const val Unauthenticated = "unauthenticated"
        const val BadRequest = "bad_request"
        const val NotFound = "not_found"
    }
}

class GraphQLDefaultErrorHandler : GraphQLErrorHandler {

    override fun handle(e : Throwable) : Nothing {
        when (e) {
            is MismatchedInputException -> throw UnexpectedResponseGraphQLException(BAD_REQUEST, "Ikke-håndtert respons")
            is FieldAccessException -> throw e.oversett()
            else -> throw UnhandledGraphQLException(INTERNAL_SERVER_ERROR, "GraphQL oppslag  feilet", e)
        }
    }
}

object GraphQLExtensions {

    private val log = LoggerUtil.getLogger(javaClass)

    const val IDENT = "ident"
    const val IDENTER = "identer"

    fun FieldAccessException.oversett() = response.errors.oversett(message)
    fun List<ResponseError>.oversett(message: String?) = oversett(firstOrNull()?.extensions?.get("code")?.toString(), message ?: "Ukjent feil").also { e ->
        log.warn("GraphQL oppslag returnerte $size feil. $this, oversatte feilkode til ${e.javaClass.simpleName}",
            this)
    }

    private fun oversett(kode : String?, msg : String) =
        when (kode) {
            Unauthorized -> UnauthorizedGraphQLException(UNAUTHORIZED, msg)
            Unauthenticated -> UnauthenticatedGraphQLException(FORBIDDEN, msg)
            BadRequest -> BadGraphQLException(BAD_REQUEST, msg)
            NotFound -> NotFoundGraphQLException(NOT_FOUND, msg)
            else -> UnhandledGraphQLException(INTERNAL_SERVER_ERROR, msg)
        }
}

class LoggingGraphQLInterceptor : GraphQlClientInterceptor {

    private val log = LoggerFactory.getLogger(LoggingGraphQLInterceptor::class.java)

    override fun intercept(request : ClientGraphQlRequest, chain : Chain) = chain.next(request).also {
        log.trace("Eksekverer {} ", request.document)
    }
}

abstract class AbstractGraphQLAdapter(client : WebClient, cfg : AbstractRestConfig,
                                      val handler : GraphQLErrorHandler = GraphQLDefaultErrorHandler()) :
    AbstractWebClientAdapter(client, cfg) {

    protected inline fun <reified T> query(graphQL : GraphQlClient, query : Pair<String, String>, vars : Map<String, List<String>>) =
        runCatching {
            (graphQL
                .documentName(query.first)
                .variables(vars)
                .retrieve(query.second)
                .toEntityList(T::class.java)
                .contextCapture()
                .block() ?: emptyList()).also {
                log.trace("Slo opp liste av {} {}", T::class.java.simpleName, it)
            }
        }.getOrElse {
            handler.handle(it)
        }

    protected inline fun <reified T> query1(graphQL : GraphQlClient, query : Pair<String, String>, vars : Map<String, String>, info : String) =
      runCatching {
          graphQL.document(query.first)
              .variables(vars)
              .execute()
              .mapNotNull {
                  if (!it.isValid) {
                      throw BadGraphQLException(BAD_REQUEST,"Ugyldig respons fra graphQL")
                  }
                  with(it.field(query.second)) {
                      if (errors.isNotEmpty()) {
                          handler.handle(errors.oversett("${errors.size} feil i responsen fra ${cfg.baseUri} ($errors"))
                      }
                      else toEntity(T::class.java)
                  }
              }
              .retryWhen(retrySpec(log, "/") { it is RecoverableGraphQLException })
              .contextCapture()
              .block()
      }.getOrElse {
          handler.handle(it)
      }


    protected inline fun <reified T> query(graphQL : GraphQlClient, query : Pair<String, String>, vars : Map<String, String>, info : String) =
        runCatching {
            graphQL
                .documentName(query.first)
                .variables(vars)
                .retrieve(query.second)
                .toEntity(T::class.java)
                .contextCapture()
                .block().also {
                    log.trace("Slo opp {} {}", T::class.java.simpleName, it)
                }
        }.getOrElse { t ->
            log.warn("Query $query feilet. $info", t)
            handler.handle(t)
        }
    }