package no.nav.aap.rest

import no.nav.aap.health.Pingable
import no.nav.aap.util.Constants.AAP
import no.nav.aap.util.Constants.TEMA
import no.nav.aap.util.LoggerUtil
import no.nav.aap.util.LoggerUtil.getLogger
import no.nav.aap.util.MDCUtil.NAV_CALL_ID
import no.nav.aap.util.MDCUtil.NAV_CALL_ID1
import no.nav.aap.util.MDCUtil.NAV_CALL_ID2
import no.nav.aap.util.MDCUtil.NAV_CONSUMER_ID
import no.nav.aap.util.MDCUtil.NAV_CONSUMER_ID2
import no.nav.aap.util.MDCUtil.callId
import no.nav.aap.util.MDCUtil.consumerId
import org.slf4j.Logger
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient

abstract class AbstractWebClientAdapter(protected val webClient: WebClient, protected open val cfg: AbstractRestConfig,private val pingClient: WebClient = webClient) : RetryAware, Pingable {

    protected val log: Logger = getLogger(javaClass)

    override fun ping() : Map<String,String> {
        if (isEnabled()) {
            pingClient
                .get()
                .uri(pingEndpoint())
                .accept(APPLICATION_JSON, TEXT_PLAIN)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess { log.trace("Ping ${pingEndpoint()} OK") }
                .doOnError { t: Throwable -> log.warn("Ping feilet", t) }
                .block()
            return emptyMap()
        }
        else return emptyMap()
    }

    override fun name() = cfg.name
    protected val baseUri = cfg.baseUri
    override fun pingEndpoint() = "${cfg.pingEndpoint}"
    override fun isEnabled() = cfg.isEnabled

    companion object {
        fun correlatingFilterFunction(defaultConsumerId: String) =
            ExchangeFilterFunction { req: ClientRequest, next: ExchangeFunction ->
                next.exchange(
                        ClientRequest.from(req)
                            .header(NAV_CONSUMER_ID, consumerId(defaultConsumerId))
                            .header(NAV_CONSUMER_ID2, consumerId(defaultConsumerId))
                            .header(NAV_CALL_ID, callId())
                            .header(NAV_CALL_ID1, callId())
                            .header(NAV_CALL_ID2, callId())
                            .build())
            }

        fun generellFilterFunction(key: String, value: () -> String) =
            ExchangeFilterFunction { req: ClientRequest, next: ExchangeFunction ->
                next.exchange(
                        ClientRequest.from(req)
                            .header(key, value.invoke())
                            .build())
            }

        fun consumerFilterFunction() = generellFilterFunction(NAV_CONSUMER_ID) { AAP }
        fun temaFilterFunction() = generellFilterFunction(TEMA) { AAP }
    }
}