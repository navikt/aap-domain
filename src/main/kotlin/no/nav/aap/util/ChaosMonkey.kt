package no.nav.aap.util

import no.nav.boot.conditionals.Cluster.Companion.currentCluster
import org.checkerframework.checker.units.qual.t
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.web.reactive.function.client.WebClientResponseException

class ChaosMonkey(private val criteria: () -> Boolean) {
    private val log = LoggerUtil.getLogger(ChaosMonkey::class.java)

    fun inhjectFault(component: Any,status: HttpStatus)  = injectFault(WebClientResponseException(status, "Tvunget feil  fra ${component.javaClass.simpleName}}", null, null, null, null))
    private fun injectFault(t: Throwable) =
        if (criteria.invoke()) {
            throw t.also {
                log.warn(it.message)
            }
        }
        else Unit
}