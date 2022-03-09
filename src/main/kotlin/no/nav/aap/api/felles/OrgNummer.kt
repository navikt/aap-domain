package no.nav.aap.api.felles

class OrgNummer(val orgnr: String) {

   init {
    require(isValid(orgnr)) { "Ugyldig organisasjonsnummer $orgnr" }
}
    companion object {
        private fun mod11OfNumberWithControlDigit(orgnr: String): Int {
            val weights = intArrayOf(3, 2, 7, 6, 5, 4, 3, 2)
            var sumForMod = 0
            orgnr.indices.forEach {
                sumForMod += (orgnr[it].code - 48) * weights[it]
            }
            val result = 11 - sumForMod % 11
            return if (result == 11) 0 else result
        }

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
    }
}