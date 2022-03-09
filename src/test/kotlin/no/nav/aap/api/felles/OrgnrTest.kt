package no.nav.aap.api.felles

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrgnrTest {
    @Test
    fun ugyldig() {
        assertThrows<IllegalArgumentException> { OrgNummer("123")}
        assertThrows<IllegalArgumentException> { OrgNummer("911111111")}
    }
    @Test
    fun gyldig() {
        OrgNummer("874652202")
    }
}