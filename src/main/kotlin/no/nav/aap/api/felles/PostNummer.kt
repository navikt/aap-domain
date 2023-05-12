package no.nav.aap.api.felles

import org.springframework.core.io.ClassPathResource

data class PostNummer(val postnr : String?, val poststed : String?) {
    constructor(postnr : String?) : this(postnr, poststeder[postnr] ?: "Ukjent poststed for $postnr")

    companion object {

        private val poststeder = try {
            ClassPathResource("postnr.txt").inputStream.bufferedReader()
                .lines()
                .map { it.split("\\s+".toRegex()) }
                .map { it[0] to it[1] }
                .toList()
                .associate { it.first to it.second }
        }
        catch (e : Exception) {
            emptyMap()
        }
    }
}