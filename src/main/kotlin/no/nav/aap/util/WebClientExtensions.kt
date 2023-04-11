package no.nav.aap.util

import no.nav.aap.api.felles.error.IrrecoverableIntegrationException
import no.nav.aap.api.felles.error.RecoverableIntegrationException
import org.slf4j.Logger
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono

object WebClientExtensions {

    inline fun <reified T> ClientResponse.response(log : Logger) =
        with(statusCode()) {
            if (is2xxSuccessful) {
                bodyToMono(T::class.java)
            }
            else if (is4xxClientError) {
                Mono.error(IrrecoverableIntegrationException("400"))
            }
            else {
                Mono.error(RecoverableIntegrationException("500"))
            }
        }
}