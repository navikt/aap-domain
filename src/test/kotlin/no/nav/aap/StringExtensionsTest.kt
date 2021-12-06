package no.nav.aap

import no.nav.aap.util.StringExtensions.limit
import no.nav.aap.util.StringExtensions.mask
import no.nav.aap.util.StringExtensions.partialMask
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StringExtensionsTest {

    @Test
    fun testMask() {
        assertEquals("***********","11111111111".mask())
    }
    @Test
    fun testLimit() {
        assertEquals("11 (9 more chars...)", "11111111111".limit(2))
    }
    @Test
    fun testPartialmask() {
        assertEquals("010203*****","01020388888".partialMask())
    }
}