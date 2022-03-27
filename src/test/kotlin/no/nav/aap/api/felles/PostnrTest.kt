package no.nav.aap.api.felles


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PostnrTest {
    @Test
    fun postnr() {
        assertEquals(PostNummer("2619").poststed,"Lillehammer".uppercase())
    }
}