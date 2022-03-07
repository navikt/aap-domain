package no.nav.aap.util

import no.nav.aap.api.felles.Fødselsnummer
import no.nav.aap.util.Constants.IDPORTEN
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException
import org.springframework.stereotype.Component


class AuthContext(private val ctxHolder: TokenValidationContextHolder) {
    fun getSubject(issuer: String = IDPORTEN) = getClaim(issuer, "pid")
    fun getFnr(issuer: String = IDPORTEN) = getSubject(issuer)?.let { Fødselsnummer(it) } ?: throw JwtTokenUnauthorizedException(IllegalArgumentException("Ikke autentisert"))
    fun getClaim(issuer: String, claim: String?) = claimSet(issuer)?.getStringClaim(claim)
    fun isAuthenticated(issuer: String = IDPORTEN) = getToken(issuer) != null
    private val context get() = ctxHolder.tokenValidationContext
    private fun getToken(issuer: String) = context?.getJwtToken(issuer)?.tokenAsString
    private fun claimSet(issuer: String) = context?.getClaims(issuer)
    override fun toString() = "${javaClass.simpleName} [ctxHolder=$ctxHolder]"
}