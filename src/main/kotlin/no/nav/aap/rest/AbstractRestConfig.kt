package no.nav.aap.rest

import no.nav.aap.util.URIUtil.uri
import java.net.URI

abstract class AbstractRestConfig(val baseUri: URI, protected val pingPath: String, name: String = baseUri.host,  isEnabled: Boolean) : AbstractConfig(name,isEnabled){
    val pingEndpoint = uri(baseUri, pingPath)
    override fun toString() = "${javaClass.simpleName} [name=$name, isEnabled=$isEnabled, pingPath=$pingPath,enabled=$isEnabled,baseUri=$baseUri]"
}

abstract class AbstractConfig(val name: String, val isEnabled: Boolean)