package no.nav.aap.rest

import no.nav.aap.util.URIUtil
import java.net.URI

abstract class AbstractRestConfig(val baseUri: URI, protected val pingPath: String, val isEnabled: Boolean) {
    val pingEndpoint = URIUtil.uri(baseUri, pingPath)
    val name = baseUri.host
    override fun toString() = "${javaClass.simpleName} [pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri]"
}