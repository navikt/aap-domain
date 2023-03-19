package no.nav.aap.util

import no.nav.boot.conditionals.Cluster.Companion.currentCluster
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.web.reactive.function.client.WebClientResponseException

class ChaosMonkey(private val criteria: () -> Boolean) {
    private val log = LoggerUtil.getLogger(ChaosMonkey::class.java)

    fun inhject(component: Any,status: HttpStatus = BAD_GATEWAY)  =
        if (criteria.invoke()) {
            throw WebClientResponseException(status, "Tvunget feil i $currentCluster fra ${component.javaClass.simpleName}}", null, null, null, null).also {
                log.warn(it.message)
            }
        }
        else {
            Unit
        }
}