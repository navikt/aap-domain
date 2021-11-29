package no.nav.aap.rest

import no.nav.aap.health.Pingable
import no.nav.aap.util.Constants
import no.nav.aap.util.MDCUtil
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient

abstract class AbstractWebClientAdapter(protected val webClient: WebClient, protected open val cfg: AbstractRestConfig) : RetryAware, Pingable {
    override fun ping()  {
        webClient
            .get()
            .uri(pingEndpoint())
            .accept(APPLICATION_JSON, TEXT_PLAIN)
            .retrieve()
            .onStatus({ obj: HttpStatus -> obj.isError }) { obj: ClientResponse -> obj.createException() }
            .toBodilessEntity()
            .block()
    }

    override fun name() = cfg.name
    protected val baseUri = cfg.baseUri
    override fun pingEndpoint() = cfg.pingEndpoint

    companion object {
        fun correlatingFilterFunction() =
            ExchangeFilterFunction { req: ClientRequest, next: ExchangeFunction ->
                next.exchange(
                        ClientRequest.from(req)
                            .header(MDCUtil.NAV_CONSUMER_ID, MDCUtil.consumerId())
                            .header(MDCUtil.NAV_CALL_ID, MDCUtil.callId())
                            .header(MDCUtil.NAV_CALL_ID1, MDCUtil.callId())
                            .build()
                             )
            }

        fun temaFilterFunction() =
            ExchangeFilterFunction { req: ClientRequest, next: ExchangeFunction ->
                next.exchange(
                        ClientRequest.from(req)
                            .header(Constants.TEMA, Constants.AAP)
                            .build()
                             )
            }
    }
}