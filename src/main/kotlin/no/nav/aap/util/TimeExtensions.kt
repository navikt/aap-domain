package no.nav.aap.util

import java.time.Instant.ofEpochMilli
import java.time.LocalDateTime
import java.time.LocalDateTime.ofInstant
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*

object TimeExtensions {
    fun Long.format(fmt: String = "yyyy-MM-dd HH:mm:ss") =  ofInstant(ofEpochMilli(this), systemDefault()).format(ofPattern(fmt))

    fun Date.toLocalDateTime()  =
        ofEpochMilli(this.getTime())
            .atZone(systemDefault())
            .toLocalDateTime()
}