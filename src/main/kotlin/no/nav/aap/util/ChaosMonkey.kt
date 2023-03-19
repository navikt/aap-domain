package no.nav.aap.util

import no.nav.boot.conditionals.Cluster.Companion.currentCluster
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.web.reactive.function.client.WebClientResponseException

class ChaosMonkey(private val criteria: () -> Boolean, private val status: HttpStatus = BAD_GATEWAY) {
    private val log = LoggerUtil.getLogger(ChaosMonkey::class.java)

    fun inhject(component: Any)  =
        if (criteria.invoke()) {
            throw WebClientResponseException(status, "Tvunget feil i $currentCluster fra ${component.javaClass.simpleName}}", null, null, null, null).also {
                log.warn(it.message)
            }
        }
        else {
            Unit
        }
}