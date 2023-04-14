package no.nav.aap.util

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.util.Base64.getEncoder
import java.util.Locale.getDefault
import java.util.Optional
import kotlin.math.min
import kotlin.text.Charsets.UTF_8
import org.springframework.core.env.Environment
import no.nav.boot.conditionals.EnvUtil

object EnvExtensions {

    fun Environment.isGcp() = EnvUtil.isGcp(this)
    fun Environment.isProd() = EnvUtil.isProd(this)
    fun Environment.isDev() = EnvUtil.isDev(this)
    fun Environment.isDevOrLocal() = EnvUtil.isDevOrLocal(this)

}
object StringExtensions {
    private const val DEFAULT_LENGTH = 50

    fun <T, U> List<T>.interseksjon(l: List<U>, predikat: (T, U) -> Boolean) =
        filter { m -> l.any { predikat(m, it) } }

    fun <T> List<T>.størrelse(str: String) = if (size == 1) ("$size $str") else "$size ${str}er"
    fun <T> Array<T>.størrelse(str: String) = toList().størrelse(str)

    fun String.toLocalDate(): LocalDate = LocalDate.parse(this, ISO_LOCAL_DATE)

    fun String.decap()  = replaceFirstChar { it.lowercase(getDefault())}
    fun String.cap()  = replaceFirstChar { it.uppercase(getDefault())}

    fun String.partialMask(mask: Char = '*') : String {
        val start = length.div(2)
        return replaceRange(start +1,length ,mask.toString().repeat(length - start -1))
    }

    fun String.limit( max: Int = DEFAULT_LENGTH) : String  {
        val ny = substring(0, min(max, length))
        if (ny.length != length) {
            return ny.plus(" (").plus(length - max).plus(" more chars...)")
        }
        return ny
    }

    fun String.asBearer() = "Bearer $this"
    fun String.limit(bytes: ByteArray, max: Int = DEFAULT_LENGTH) = String(bytes).limit(max)
    fun String.mask(mask: String = "*") = replace(("[^\\.]").toRegex(), mask)

    fun <T> Optional<T>.unwrap(): T? = orElse(null)

    fun String.jsonPrettify(mapper: ObjectMapper) = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(this,Any::class.java))

    fun Any.toEncodedJson(mapper: ObjectMapper) = toJson(mapper).toByteArray().encode()

    fun Any.toJson(mapper: ObjectMapper) = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

    fun ByteArray.encode() = getEncoder().encodeToString(this)

    fun String.encode() = toByteArray(UTF_8).encode()


}