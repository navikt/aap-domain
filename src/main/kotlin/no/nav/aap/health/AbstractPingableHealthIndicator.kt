package no.nav.aap.health

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.Health.down
import org.springframework.boot.actuate.health.HealthIndicator

abstract class AbstractPingableHealthIndicator(private val pingable: Pingable) : HealthIndicator {
    override fun health()  =
        try {
            pingable.ping()
            up()
        } catch (e: Exception) {
            down(e)
        }

    private fun up() = with(pingable) { Health.up().withDetail("url", pingEndpoint()).build() }
    private fun down(e: Exception)  = with(pingable) { down().withDetail("url", pingEndpoint()).withException(e).build() }

    override fun toString() = "${javaClass.simpleName} [pingable=$pingable]"
}