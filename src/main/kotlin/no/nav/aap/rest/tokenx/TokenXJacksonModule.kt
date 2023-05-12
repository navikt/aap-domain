package no.nav.aap.rest.tokenx

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.util.VersionUtil.versionFor
import com.fasterxml.jackson.databind.module.SimpleModule
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenResponse

class TokenXJacksonModule : SimpleModule() {

    override fun setupModule(ctx : SetupContext?) =
        SimpleModule(versionFor(TokenXJacksonModule::class.java))
            .setMixInAnnotation(OAuth2AccessTokenResponse::class.java, IgnoreUnknownMixin::class.java)
            .setupModule(ctx)

    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface IgnoreUnknownMixin
}