package no.nav.aap.util

import java.time.Instant.ofEpochMilli
import java.time.LocalDateTime.ofInstant
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter.ofPattern

object TimeUtil {
    fun Long.format(fmt: String = "yyyy-MM-dd HH:mm:ss") =  ofInstant(ofEpochMilli(this), systemDefault()).format(ofPattern(fmt))
}