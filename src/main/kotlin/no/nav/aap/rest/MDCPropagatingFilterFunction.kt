package no.nav.aap.rest

import org.slf4j.MDC
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

class MDCPropagatingFilterFunction : ExchangeFilterFunction {
    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
            // here runs on main(request's) thread
            val map  = MDC.getCopyOfContextMap()
            return next.exchange(request)
                .log("Exchanging MDC values")
                .doOnNext {
                    if (map != null) {
                        MDC.setContextMap(map)
                    }
                }
    }
}