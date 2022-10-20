package no.nav.aap.api.felles

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.aap.util.LoggerUtil
import no.nav.aap.util.LoggerUtil.getLogger
import no.nav.boot.conditionals.Cluster
import no.nav.boot.conditionals.Cluster.currentCluster
import no.nav.boot.conditionals.EnvUtil.DEV_GCP
import org.apache.commons.lang3.StringUtils.substring
import org.slf4j.Logger

data class OrgNummer(@get:JsonValue val orgnr: String) {

    protected val log: Logger = getLogger(javaClass)

    init {
       if (!currentCluster().equals(Cluster.DEV_GCP)) {
           log.trace("Vi er i cluster ${currentCluster()}, gjør validering av $orgnr")
           require(orgnr.length == 9) { "Orgnr må ha lengde 9, $orgnr har lengde ${orgnr.length} "}
           require(orgnr.startsWith("8") || orgnr.startsWith("9")) { "Orgnr må begynne med 8 eller 9"}
           require(orgnr[8].code - 48 == mod11(orgnr.substring(0, 8))) {"${orgnr[8]} feilet mod11 validering"}
           //require(isValid(orgnr)) { "Ugyldig organisasjonsnummer $orgnr, må være 9 tegn lang og begynne med 8 eller 9 og validere mod11" }
       }
        else {
            log.trace("Vi er i cluster ${currentCluster()}, ingen validering av $orgnr")
       }
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