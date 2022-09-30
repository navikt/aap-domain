package no.nav.aap.health
import java.net.URI

interface Pingable {
    fun ping()
    fun pingEndpoint(): URI
    fun name(): String
    fun isEnabled(): Boolean
}