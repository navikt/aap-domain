package no.nav.aap.util

import no.nav.aap.api.felles.error.IrrecoverableIntegrationException
import no.nav.aap.api.felles.error.RecoverableIntegrationException
import org.slf4j.Logger
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.kotlin.core.publisher.toMono

object WebClientExtensions {

    inline fun <reified T> ClientResponse.toResponse(log: Logger) =
        with(statusCode()){
            if (is2xxSuccessful) {
                bodyToMono(T::class.java)
            }
            else if (is4xxClientError)
                bodyToMono<Any>().flatMap {
                    log.warn("$it")
                    IrrecoverableIntegrationException("$it").toMono()
                }
            else bodyToMono<Any>().flatMap {
                RecoverableIntegrationException("$it").toMono()
            }
        }
}