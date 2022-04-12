package no.nav.aap.rest

import no.nav.security.token.support.core.api.Unprotected
import org.springframework.core.annotation.AliasFor
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.CLASS

@RestController
@MustBeDocumented
@Unprotected
@Target(ANNOTATION_CLASS, CLASS)
@Retention(RUNTIME)
@RequestMapping
annotation class ProtectedRestController(@get: AliasFor(annotation = RequestMapping::class, attribute = "value") val value:  Array<String> = ["/"],
                                         @get: AliasFor(annotation = RequestMapping::class, attribute = "produces") val produces: Array<String> = [APPLICATION_JSON_VALUE])