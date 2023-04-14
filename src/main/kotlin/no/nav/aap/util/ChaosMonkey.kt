package no.nav.aap.util

import kotlin.random.Random.Default.nextInt
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.kotlin.core.publisher.toMono
import no.nav.aap.api.felles.error.IrrecoverableIntegrationException
import no.nav.aap.api.felles.error.RecoverableIntegrationException
import no.nav.aap.util.ChaosMonkey.MonkeyExceptionType.RECOVERABLE
import no.nav.boot.conditionals.Cluster
import no.nav.boot.conditionals.Cluster.Companion.currentCluster
import no.nav.boot.conditionals.Cluster.Companion.devClusters

class ChaosMonkey(private val defaultCriteria: () -> Boolean = defaultCrit()) {

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
                RECOVERABLE -> RecoverableIntegrationException("Chaos Monkey recoverable exception i $currentCluster for $msg")
                IRRECOVERABLE -> IrrecoverableIntegrationException("Chaos Monkey irrrecoverable exception i $currentCluster for $msg")
            }
    }

    fun injectFault( component: String, type: MonkeyExceptionType = RECOVERABLE,criteria: () -> Boolean = defaultCriteria) =
        if (criteria.invoke()) {
            throw type.toException(component).also {
                log.warn(it.message,it)
            }
        }
        else Unit

    companion object {
        fun defaultCrit(clusters: Array<Cluster> = devClusters(), n: Int = 5) = { -> nextInt(1, n) == 1 && currentCluster in clusters.asList() }
        const val MONKEY = "chaos-monkey"
        private  val NO_MONKEY = { false }
    }
}