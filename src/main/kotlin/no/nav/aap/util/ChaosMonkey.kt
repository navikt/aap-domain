package no.nav.aap.util

import java.net.URI
import kotlin.random.Random.Default.nextInt
import no.nav.aap.api.felles.error.IrrecoverableIntegrationException
import no.nav.aap.api.felles.error.RecoverableIntegrationException
import no.nav.aap.rest.AbstractWebClientAdapter
import no.nav.aap.util.ChaosMonkey.MonkeyExceptionType.RECOVERABLE
import no.nav.boot.conditionals.Cluster
import no.nav.boot.conditionals.Cluster.Companion
import no.nav.boot.conditionals.Cluster.Companion.currentCluster
import no.nav.boot.conditionals.Cluster.Companion.devClusters
import org.checkerframework.checker.units.qual.t
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.kotlin.core.publisher.toMono

class ChaosMonkey(private val defaultCriteria: () -> Boolean = NO_MONKEY) {

    private val log = LoggerUtil.getLogger(ChaosMonkey::class.java)

    fun chaosMonkeyRequestFilterFunction( criteria: () -> Boolean = defaultCriteria, type: MonkeyExceptionType = RECOVERABLE) = ExchangeFilterFunction.ofRequestProcessor {
        if (criteria.invoke() && !it.url().host.contains("microsoft")) {
            log.trace("Tvinger fram feil for ${it.url()}")
            with(type.toException(it.url().toString())) {
                log.info(message, this)
                toMono()
            }
        }
        else {
            log.trace("Tvinger IKKE fram feil  for ${it.url()}")
            it.toMono()
        }
    }
    enum class MonkeyExceptionType  { RECOVERABLE, IRRECOVERABLE;
        fun toException(msg: String) =
            when(this) {
                RECOVERABLE -> RecoverableIntegrationException("Chaos Monkey recoverable exception i ${Cluster.currentCluster} for $msg")
                IRRECOVERABLE -> IrrecoverableIntegrationException("Chaos Monkey irrrecoverable exception i ${Cluster.currentCluster} for $msg")
            }
    }

    fun injectFault( component: String, type: MonkeyExceptionType = RECOVERABLE,criteria: () -> Boolean = defaultCriteria) =
        if (criteria.invoke()) {
            throw type.toException(component).also {
                log.warn(it.message,it)
            }
        }
        else Unit
    fun criteria(clusters: Array<Cluster> = devClusters(), n: Int = 5) = { -> nextInt(1, n) == 1 && currentCluster in clusters.asList() }

    companion object {
        const val MONKEY = "chaos-monkey"
        private  val NO_MONKEY = { false }
    }
}