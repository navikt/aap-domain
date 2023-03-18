package no.nav.aap.rest

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import java.io.IOException
import java.net.URI
import java.time.Duration
import java.util.*
import java.util.function.Predicate
import javax.swing.text.html.HTML.Tag.BASE
import no.nav.aap.util.Metrics
import no.nav.aap.util.URIUtil.uri
import org.apache.commons.lang3.exception.ExceptionUtils.*
import org.slf4j.Logger
import org.springframework.boot.context.properties.bind.DefaultValue
import org.springframework.boot.convert.DurationStyle.*
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.WebClientResponseException.BadRequest
import org.springframework.web.reactive.function.client.WebClientResponseException.Forbidden
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound
import org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized
import reactor.util.retry.Retry.*

abstract class AbstractRestConfig(val baseUri: URI, val pingPath: String, name: String = baseUri.host, isEnabled: Boolean, val retry: RetryConfig) : AbstractConfig(name,isEnabled){
    val pingEndpoint = uri(baseUri, pingPath)


    data class RetryConfig(
            @DefaultValue(DEFAULT_RETRIES) val retries: Long,
            @DefaultValue(DEFAULT_DELAY) val delayed: Duration) {
        companion object {
            private const val DEFAULT_RETRIES = "3"
            private const val DEFAULT_DELAY = "100ms"
            val DEFAULT = RetryConfig(DEFAULT_RETRIES.toLong(), detectAndParse(DEFAULT_DELAY))
        }
    }

    fun retrySpec(log: Logger, path: String = "/",metrikker: Metrics,exceptionsFilter: Predicate<in Throwable> = DEFAULT_EXCEPTIONS_PREDICATE) =
         fixedDelay(retry.retries, retry.delayed)
            .filter(exceptionsFilter)
            .onRetryExhaustedThrow {
                _, s -> s.failure().also {
                metrikker.inc(METRIKKNAVN, BASE,"$baseUri",PATH,path, EXCEPTION,"${s.name()}",TYPE, EXHAUSTED)
                log.warn("Retry mot $baseUri/$path gir opp grunnet exception ${s.name()} etter ${s.totalRetries()} forsøk") }
            }
             .doBeforeRetry {
                 if (it.totalRetries() == 0L)  {
                     log.info("1. retry kall mot $baseUri$/path grunnet exception ${it.name()} og melding ${it.failure().message}")
                 }
                 else   {
                     log.info("${it.totalRetries()}. retry mot $baseUri/$path grunnet exception ${it.name()} og melding ${it.failure().message} feilet etter ${it.totalRetriesInARow() + 1}. forsøk, prøver igjen")
                 }
             }
            .doAfterRetry  {
                log.info("${it.totalRetries()}. retry mot $baseUri/$path grunnet exception ${it.name()}")
            }

    private fun RetrySignal.name() = failure().javaClass.simpleName



    companion object  {
        private const val METRIKKNAVN = "webclient"
        private const val BASE = "base"
        private const val PATH = "path"
        private const val EXCEPTION = "exception"
        private const val TYPE = "type"
        private const val EXHAUSTED = "exhausted"
        private const val SUCCESS = "success"
        private val DEFAULT_EXCEPTIONS_PREDICATE = Predicate<Throwable> { hasCause(it,IOException::class.java) || (it is WebClientResponseException && it !is BadRequest && it !is Unauthorized && it !is NotFound && it !is Forbidden) }
    }
    override fun toString() = "${javaClass.simpleName} [name=$name, isEnabled=$isEnabled, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri]"
}

abstract class AbstractConfig(val name: String, val isEnabled: Boolean)