package no.nav.aap.rest

import java.io.IOException
import java.net.URI
import java.time.Duration
import java.util.*
import java.util.function.BiFunction
import java.util.function.Predicate
import no.nav.aap.util.URIUtil.uri
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.commons.lang3.exception.ExceptionUtils.*
import org.slf4j.Logger
import org.springframework.boot.context.properties.bind.DefaultValue
import org.springframework.boot.convert.DurationStyle.*
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.WebClientResponseException.Forbidden
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound
import org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized
import reactor.util.retry.Retry.*
import reactor.util.retry.RetryBackoffSpec

abstract class AbstractRestConfig(val baseUri: URI, val pingPath: String, name: String = baseUri.host,  isEnabled: Boolean, val retry: RetryConfig) : AbstractConfig(name,isEnabled){
    val pingEndpoint = uri(baseUri, pingPath)


    data class RetryConfig(@DefaultValue(DEFAULT_RETRIES)  val retries: Long,
                           @DefaultValue(DEFAULT_DELAY)  val delayed: Duration) {
        companion object {
            const val DEFAULT_RETRIES = "3"
            const val DEFAULT_DELAY = "100ms"
            val DEFAULT = RetryConfig(DEFAULT_RETRIES.toLong(), detectAndParse(DEFAULT_DELAY))
        }
    }

    fun retrySpec(log: Logger,exceptionsFilter: Predicate<in Throwable> = DEFAULT_EXCEPTIONS_PREDICATE) =
         fixedDelay(retry.retries, retry.delayed)
            .filter(exceptionsFilter)
            .onRetryExhaustedThrow { _, s -> s.failure().also { log.warn("Retry kall mot  $baseUri gir opp med  ${s.failure().javaClass.simpleName} etter ${s.totalRetries()} forsøk") } }
            .doAfterRetry  { s -> log.info("Retry kall mot $baseUri grunnet exception ${s.failure().javaClass.simpleName} og melding ${s.failure().message} gjort for ${s.totalRetriesInARow() + 1} gang") }
            .doBeforeRetry { s -> log.info("Retry kall mot $baseUri grunnet exception ${s.failure().javaClass.simpleName} og melding ${s.failure().message} for ${s.totalRetriesInARow() + 1} gang, prøver igjen") }


    companion object  {
        private val DEFAULT_EXCEPTIONS_PREDICATE = Predicate<Throwable> { hasCause(it,IOException::class.java) || (it is WebClientResponseException && it !is Unauthorized && it !is NotFound && it !is Forbidden) }
    }
    override fun toString() = "${javaClass.simpleName} [name=$name, isEnabled=$isEnabled, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri]"
}

abstract class AbstractConfig(val name: String, val isEnabled: Boolean)