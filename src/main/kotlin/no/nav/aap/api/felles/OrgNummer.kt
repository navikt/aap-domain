package no.nav.aap.api.felles

import com.fasterxml.jackson.annotation.JsonValue

data class OrgNummer(@get:JsonValue val orgnr: String) {

   init {
    require(isValid(orgnr)) { "Ugyldig organisasjonsnummer $orgnr" }
}
    companion object {
        private val WEIGHTS =  intArrayOf(3, 2, 7, 6, 5, 4, 3, 2)


        fun isValid(orgnr: String): Boolean {
            if (orgnr.length != 9) {
                return false
            }
            if (!(orgnr.startsWith("8") || orgnr.startsWith("9"))) {
                return false
            }
            val value = mod11OfNumberWithControlDigit(orgnr.substring(0, 8))
            return orgnr[8].code - 48 == value
        }
        private fun mod11OfNumberWithControlDigit(orgnr: String) =
            with(11 - orgnr.indices.sumOf { (orgnr[it].code - 48) * WEIGHTS[it] } % 11) {
                if (this == 11) 0 else this
            }
    }
}