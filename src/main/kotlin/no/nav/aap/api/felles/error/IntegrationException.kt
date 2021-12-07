package no.nav.aap.api.felles.error

import java.net.URI

class IntegrationException(msg: String?, uri: URI? = null, cause: Throwable? = null) : RuntimeException(msg, cause)