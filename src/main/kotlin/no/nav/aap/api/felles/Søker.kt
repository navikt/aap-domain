package no.nav.aap.api.felles

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import no.nav.aap.util.StringExtensions.partialMask
import java.time.Duration
import java.time.LocalDate

data class Navn(val fornavn: String?, val mellomnavn: String?, val etternavn: String?){
    @JsonIgnore
    val navn = listOfNotNull(fornavn, mellomnavn, etternavn).joinToString(separator = " ").trim()
}
data class Fødselsnummer(@get:JsonValue val fnr: String) {
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
    UTLAND("NAV 11-03.07", "Søknad om å beholde AAP ved opphold i utlandet"),
    STANDARD("NAV 11-13.05", "Søknad om AAP")
}