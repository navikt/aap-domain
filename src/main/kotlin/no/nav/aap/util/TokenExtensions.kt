package no.nav.aap.util

import java.net.URI
import no.nav.aap.util.StringExtensions.asBearer
import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.core.exceptions.JwtTokenMissingException

object TokenExtensions {

    fun OAuth2AccessTokenService.bearerToken(props : ClientProperties?, url : URI) =
        props?.let {
            getAccessToken(it).accessToken.asBearer()
        } ?: throw JwtTokenMissingException("Ingen konfigurasjon for $url")
}