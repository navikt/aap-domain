package no.nav.aap.util

import java.util.*

object CallIdGenerator {
    fun create() = "${UUID.randomUUID()}"
}