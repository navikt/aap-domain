package no.nav.aap.rest.tokenx

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono
import no.nav.aap.util.AuthContext
import no.nav.aap.util.LoggerUtil
import no.nav.aap.util.StringExtensions.asBearer
import no.nav.boot.conditionals.EnvUtil.CONFIDENTIAL
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.ClientConfigurationPropertiesMatcher

class TokenXFilterFunction(
        private val configs: ClientConfigurationProperties,
        private val service: OAuth2AccessTokenService,
        private val matcher: ClientConfigurationPropertiesMatcher,
        private val authContext: AuthContext) : ExchangeFilterFunction {
    private val log = LoggerUtil.getLogger(javaClass)

    override fun filter(req: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        val url = req.url()
        val cfg = matcher.findProperties(configs, url).orElse(null)
        if (cfg != null && authContext.isAuthenticated()) {
            log.trace("Gjør token exchange for $url")
            val token = service.getAccessToken(cfg).accessToken
            log.trace("Token exchange for {} OK", url)
            log.trace(CONFIDENTIAL,"Token er {}", token)
            return next.exchange(ClientRequest.from(req).header(AUTHORIZATION, token.asBearer()).build())
        }
        log.trace("Ingen token exchange for {}", url)
        return next.exchange(ClientRequest.from(req).build())
    }

    override fun toString() =
        "${javaClass.simpleName} [[configs=$configs,authContext=$authContext,service=$service,matcher=$matcher]"
}