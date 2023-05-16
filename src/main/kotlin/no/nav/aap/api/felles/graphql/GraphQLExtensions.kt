package no.nav.aap.api.felles.graphql

import org.springframework.graphql.ResponseError
import org.springframework.graphql.client.FieldAccessException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import no.nav.aap.api.felles.error.IrrecoverableGraphQLException.BadGraphQLException
import no.nav.aap.api.felles.error.IrrecoverableGraphQLException.NotFoundGraphQLException
import no.nav.aap.api.felles.error.IrrecoverableGraphQLException.UnauthenticatedGraphQLException
import no.nav.aap.api.felles.error.IrrecoverableGraphQLException.UnauthorizedGraphQLException
import no.nav.aap.api.felles.error.RecoverableGraphQLException.UnhandledGraphQLException
import no.nav.aap.util.LoggerUtil

object GraphQLExtensions {

    private const val Unauthorized = "unauthorized"
    private const val Unauthenticated = "unauthenticated"
    private const val BadRequest = "bad_request"
    private const val NotFound = "not_found"
    private val log = LoggerUtil.getLogger(javaClass)
    fun FieldAccessException.oversett() = response.errors.oversett(message)
    private fun List<ResponseError>.oversett(message: String?) = oversett(firstOrNull()?.extensions?.get("code")?.toString(), message ?: "Ukjent feil").also { e ->
        log.warn("GraphQL oppslag returnerte $size feil. $this, oversatte feilkode til ${e.javaClass.simpleName}", this)
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