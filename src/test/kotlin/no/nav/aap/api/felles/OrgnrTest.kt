package no.nav.aap.api.felles

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [ObjectMapper::class])
class OrgnrTest {
    @Autowired
    lateinit var mapper: ObjectMapper
    @Test
    fun ugyldig() {
        assertThrows<IllegalArgumentException> { OrgNummer("123")}
        assertThrows<IllegalArgumentException> { OrgNummer("911111111")}
    }
    @Test
    fun gyldig() {
        serdeser(TestClass(OrgNummer("874652202")),true)
    }


    private fun serdeser(a: Any, print: Boolean = false) {
        mapper.registerKotlinModule()
        val ser = mapper.writeValueAsString(a)
        if (print) println(ser)
        val deser = mapper.readValue(ser, a::class.java)
        if (print) println(deser)
        assertThat(a).isEqualTo(deser)
    }

    data class TestClass(val orgnr: OrgNummer)
}