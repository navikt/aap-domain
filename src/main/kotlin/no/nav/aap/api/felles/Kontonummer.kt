package no.nav.aap.api.felles

import com.fasterxml.jackson.annotation.JsonValue
import java.lang.Character.getNumericValue
import no.nav.boot.conditionals.Cluster.Companion.currentCluster
import no.nav.boot.conditionals.EnvUtil.DEV_GCP

data class Kontonummer(@get:JsonValue val kontonr: String) {

    init {
        if (!currentCluster().equals(DEV_GCP)) {
            require(isValid(kontonr)) { "Ugyldig kontonummer $kontonr" }
        }
    }

    companion object {
        private val WEIGHTS = intArrayOf(2, 3, 4, 5, 6, 7,2,3,4,5)

        private fun isValid(kontonr: String) =
            with(kontonr) {
                length == 11 && getNumericValue(this[10])== mod11(substring(0, 10).reversed())
            }

        private fun mod11(kontonr: String) =
            with( 11 -kontonr.indices.sumOf {
                getNumericValue(kontonr[it]) * WEIGHTS[it] } % 11) {
                if (this == 11) 0 else this
            }
    }
}