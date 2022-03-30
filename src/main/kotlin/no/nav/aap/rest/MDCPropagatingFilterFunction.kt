package no.nav.aap.rest

import no.nav.aap.util.LoggerUtil
import org.slf4j.MDC
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

class MDCPropagatingFilterFunction : ExchangeFilterFunction {
    private val log = LoggerUtil.getLogger(javaClass)
    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
            val map  = MDC.getCopyOfContextMap()
            return next.exchange(request)
                .doOnNext {
                    if (map != null) {
                        log.trace("Setter  map fo neste tråd")
                        MDC.setContextMap(map)
                    }
                }
    }
}