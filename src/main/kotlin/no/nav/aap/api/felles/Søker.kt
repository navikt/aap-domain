package no.nav.aap.api.felles

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import com.neovisionaries.i18n.CountryCode
import no.nav.aap.util.StringExtensions.partialMask
import java.time.Duration
import java.time.LocalDate

data class Søker(val fnr: Fødselsnummer, val navn: Navn?)
data class Navn(val fornavn: String?, val mellomnavn: String?, val etternavn: String?){
    @JsonIgnore
    val navn = listOfNotNull(fornavn, mellomnavn, etternavn).joinToString(separator = " ").trim()
}
data class Fødselsnummer(@JvmField @JsonValue val fnr: String) {
    override fun toString() = "${javaClass.simpleName} [fnr=${fnr.partialMask()}]"
}

data class Periode(val fom: LocalDate, val tom: LocalDate?) {
    fun varighetDager() = tom?.let { Duration.between(fom.atStartOfDay(), it.atStartOfDay()).toDays() } ?: -1
}

data class UtenlandsSøknadKafka(val søker: Søker, val land: CountryCode, val periode: Periode) {
    val fulltNavn = søker.navn?.navn
}