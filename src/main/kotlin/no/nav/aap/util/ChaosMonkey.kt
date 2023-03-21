package no.nav.aap.util

import no.nav.aap.api.felles.error.RecoverableIntegrationException
import org.checkerframework.checker.units.qual.t
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException

class ChaosMonkey(private val defaultCriteria: () -> Boolean) {
    private val log = LoggerUtil.getLogger(ChaosMonkey::class.java)

    fun injectFault( component: String, t: Throwable,criteria: () -> Boolean = defaultCriteria) =
        if (criteria.invoke()) {
            throw t.also {
                log.warn("Chaos Monkey exception ${t.javaClass.simpleName} fra $component",t)
            }
        }
        else Unit
}