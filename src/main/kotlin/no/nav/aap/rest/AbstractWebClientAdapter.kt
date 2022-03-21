package no.nav.aap.rest

import no.nav.aap.health.Pingable
import no.nav.aap.util.Constants
import no.nav.aap.util.LoggerUtil
import no.nav.aap.util.MDCUtil
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient

abstract class AbstractWebClientAdapter(protected val webClient: WebClient, protected open val cfg: AbstractRestConfig) : RetryAware, Pingable {

    protected val log: Logger = LoggerUtil.getLogger(javaClass)

    override fun ping()  {
        webClient
            .get()
            .uri(pingEndpoint())
            .accept(APPLICATION_JSON, TEXT_PLAIN)
            .retrieve()
            .toBodilessEntity()
            .doOnError { t: Throwable -> log.warn("Ping oppslag  feilet", t) }
            .block()
    }

    override fun name() = cfg.name
    protected val baseUri = cfg.baseUri
    override fun pingEndpoint() = cfg.pingEndpoint

    companion object {
        fun correlatingFilterFunction(defaultConsumerId: String) =
            ExchangeFilterFunction { req: ClientRequest, next: ExchangeFunction ->
                next.exchange(
                        ClientRequest.from(req)
                            .header(MDCUtil.NAV_CONSUMER_ID, MDCUtil.consumerId(defaultConsumerId))
                            .header(MDCUtil.NAV_CONSUMER_ID2, MDCUtil.consumerId(defaultConsumerId))
                            .header(MDCUtil.NAV_CALL_ID, MDCUtil.callId())
                            .header(MDCUtil.NAV_CALL_ID1, MDCUtil.callId())
                            .header(MDCUtil.NAV_CALL_ID2, MDCUtil.callId())
                            .build())
            }

        fun generellFilterFunction(key: String, value: () -> String) =
            ExchangeFilterFunction { req: ClientRequest, next: ExchangeFunction ->
                next.exchange(
                        ClientRequest.from(req)
                            .header(key, value.invoke())
                            .build())
            }

        fun consumerFilterFunction() = generellFilterFunction(Constants.NAV_CONSUMER_ID) { Constants.AAP }
        fun temaFilterFunction() = generellFilterFunction(Constants.TEMA) { Constants.AAP }
    }
}