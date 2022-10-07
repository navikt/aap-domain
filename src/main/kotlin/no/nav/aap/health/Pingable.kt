package no.nav.aap.health
import java.net.URI

interface Pingable {
    fun ping(): Map<String,String>
    fun pingEndpoint(): String
    fun name(): String
    fun isEnabled(): Boolean
}