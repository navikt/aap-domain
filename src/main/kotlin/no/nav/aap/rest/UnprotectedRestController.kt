package no.nav.aap.rest

import no.nav.boot.conditionals.ConditionalOnNotProd
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE


@RestController
@RequestMapping
@Unprotected
@MustBeDocumented
@ConditionalOnNotProd
@Target(TYPE, CLASS)
@Retention(RUNTIME)
annotation class UnprotectedRestController(vararg val value: String = [],
                                           val produces: Array<String> = [APPLICATION_JSON_VALUE])