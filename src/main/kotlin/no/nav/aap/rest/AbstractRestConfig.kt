package no.nav.aap.rest

import no.nav.aap.util.URIUtil
import java.net.URI

abstract class AbstractRestConfig(val baseUri: URI, protected val pingPath: String,val  name: String = baseUri.host, val isEnabled: Boolean) {
    val pingEndpoint = URIUtil.uri(baseUri, pingPath)
    override fun toString() = "${javaClass.simpleName} [pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri]"
}