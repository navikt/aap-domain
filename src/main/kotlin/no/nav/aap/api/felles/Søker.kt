package no.nav.aap.api.felles

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import no.nav.aap.util.StringExtensions.decap
import no.nav.aap.util.StringExtensions.partialMask
import java.time.Duration
import java.time.LocalDate

data class Navn(val fornavn: String?, val mellomnavn: String?, val etternavn: String?){
    @JsonIgnore
    val navn = listOfNotNull(fornavn, mellomnavn, etternavn).joinToString(separator = " ").trim()
}

data class AktørId(@get:JsonValue val id: String) {
    init {
        require(id.length == 13) { "Aktørid $id er ikke 13 siffer" }
    }
}

data class Fødselsnummer(@get:JsonValue val fnr: String) {
    init {
        require(fnr.length == 11) { "Fødselsnummer $fnr er ikke 11 siffer" }
        require(mod11(W1, fnr) == fnr[9] - '0') { "Første kontrollsiffer $fnr[9] ikke validert" }
        require(mod11(W2, fnr) == fnr[10] - '0'){ "Andre kontrollsiffer $fnr[10] ikke validert" }
    }

    companion object {
        private val W1 = intArrayOf(2, 5, 4, 9, 8, 1, 6, 7, 3)
        private val W2 = intArrayOf(2, 3, 4, 5, 6, 7, 2, 3, 4, 5)

        private fun mod11(weights: IntArray, fnr: String) =
            with(weights.indices.sumOf { weights[it] * (fnr[(weights.size - 1 - it)] - '0') } % 11){
                when(this) {
                    0 -> 0
                    1 -> throw IllegalArgumentException(fnr)
                    else -> 11 - this
                }
            }

    }
    override fun toString() = "${javaClass.simpleName} [fnr=${fnr.partialMask()}]"
}

data class Periode(val fom: LocalDate, val tom: LocalDate?) {
    @JsonIgnore
    val varighetDager = tom?.let { Duration.between(fom.atStartOfDay(), it.atStartOfDay()).toDays() } ?: -1
}

data class Adresse (val adressenavn: String?, val husbokstav: String?, val husnummer: String?, val postnummer: PostNummer?) {
    @JsonIgnore
    val fullAdresse = listOfNotNull(adressenavn,husbokstav,husnummer,postnummer?.postnr,postnummer?.poststed).joinToString(separator = " ")
}



enum class SkjemaType(val kode: String, val tittel: String) {
    MELDEKORT("NAV 00-10.02","Meldekort"),
    UTLAND_SØKNAD("NAV 11-03.07", "Søknad om å beholde AAP ved opphold i utlandet"),
    UTLAND_ETTERSENDING("NAVe 11-03.07", "Ettersending til ${UTLAND_SØKNAD.tittel.decap()}"),
    STANDARD("NAV 11-13.05", "Søknad om arbeidsavklaringspenger"),
    STANDARD_ETTERSENDING("NAVe 11-13.05", "Ettersendelse til søknad om arbeidsavklaringspenger")

}