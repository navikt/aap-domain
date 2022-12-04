package no.nav.aap.rest

import no.nav.aap.util.URIUtil.uri
import java.net.URI
import java.time.Duration
import org.slf4j.Logger
import org.springframework.boot.context.properties.bind.DefaultValue
import org.springframework.boot.convert.DurationStyle
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound
import org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized
import reactor.util.retry.Retry

abstract class AbstractRestConfig(val baseUri: URI, protected val pingPath: String, name: String = baseUri.host,  isEnabled: Boolean = true, val retry: RetryConfig = RetryConfig.DEFAULT) : AbstractConfig(name,isEnabled){
    val pingEndpoint = uri(baseUri, pingPath)


    data class RetryConfig(@DefaultValue(DEFAULT_RETRIES)  val retries: Long,
                           @DefaultValue(DEFAULT_DELAY)  val delayed: Duration) {
        companion object {
            const val DEFAULT_RETRIES = "3"
            const val DEFAULT_DELAY = "100ms"
            val DEFAULT = RetryConfig(DEFAULT_RETRIES.toLong(), DurationStyle.detectAndParse(DEFAULT_DELAY))
        }
    }

    fun retrySpec(log: Logger) =
        Retry.fixedDelay(retry.retries, retry.delayed)
            .filter { e -> e is WebClientResponseException && e !is Unauthorized && e !is NotFound }
            .onRetryExhaustedThrow { _, s ->  s.failure()}
            .doBeforeRetry { s -> log.warn("Retry kall mot $baseUri grunnet exception ${s.failure().javaClass.name} og melding ${s.failure().message} for ${s.totalRetriesInARow() + 1} gang, prøver igjen") }

    override fun toString() = "${javaClass.simpleName} [name=$name, isEnabled=$isEnabled, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri]"
}

abstract class AbstractConfig(val name: String, val isEnabled: Boolean)