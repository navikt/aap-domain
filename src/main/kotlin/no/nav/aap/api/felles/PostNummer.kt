package no.nav.aap.api.felles

import org.springframework.core.io.ClassPathResource

data class PostNummer private constructor (val postnr: String,val poststed: String?)  {
     constructor(postnr: String) : this(postnr, poststeder[postnr])

    companion object{
       private val poststeder = ClassPathResource("postnr.txt").inputStream.bufferedReader()
           .lines()
            .map { it.split("\\s+".toRegex()) }
            .map { it[0] to it[1] }
            .toList()
            .associate { it.first to it.second }
    }
}