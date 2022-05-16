package no.nav.aap.api.felles

import com.fasterxml.jackson.annotation.JsonValue

data class OrgNummer(@get:JsonValue val orgnr: String) {

   init {
    require(isValid(orgnr)) { "Ugyldig organisasjonsnummer $orgnr" }
}
    companion object {
        private val WEIGHTS = intArrayOf(3, 2, 7, 6, 5, 4, 3, 2)

        private fun mod11(orgnr: String) =
             with(11 - orgnr.indices.sumOf {
                (orgnr[it].code - 48) * WEIGHTS[it] } % 11) {
                if (this == 11) 0 else this
            }

        private fun isValid(orgnr: String) =
            with(orgnr) {
                length == 9 &&
                        (startsWith("8") || startsWith("9")) &&
                        this[8].code - 48 == mod11(substring(0, 8))
            }
    }
}