package no.nav.aap.api.felles.error

import java.net.URI

open class IntegrationException(msg: String?, uri: URI? = null, cause: Throwable? = null) : RuntimeException(msg, cause)

class RecoverableIntegrationException(msg: String?, uri: URI? = null, cause: Throwable? = null) : IntegrationException(msg,uri,cause)

class IrrecoverableIntegrationException(msg: String?, uri: URI? = null, cause: Throwable? = null) : IntegrationException(msg,uri,cause)