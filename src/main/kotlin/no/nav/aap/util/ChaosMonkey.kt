package no.nav.aap.util

import org.checkerframework.checker.units.qual.t
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException

class ChaosMonkey(private val defaultCriteria: () -> Boolean) {
    private val log = LoggerUtil.getLogger(ChaosMonkey::class.java)

    fun inhjectFault(component: Any,status: HttpStatus)  = injectFault(component, WebClientResponseException(status, "Tvunget feil  fra ${component.javaClass.simpleName}}", null, null, null, null))
    fun injectFault( component: Any, t: Throwable,criteria: () -> Boolean = defaultCriteria) =
        if (criteria.invoke()) {
            throw t.also {
                log.warn("Chaos Monkey exception ${component.javaClass.name}",t)
            }
        }
        else Unit
}