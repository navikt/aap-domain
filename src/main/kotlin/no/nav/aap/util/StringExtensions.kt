package no.nav.aap.util

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import kotlin.math.min

object StringExtensions {
    private const val DEFAULT_LENGTH = 50

    fun String.partialMask(mask: Char = '*') : String {
        val start = length.div(2)
        return replaceRange(start +1,length ,mask.toString().repeat(length - start -1))
    }

    fun String.limit( max: Int = DEFAULT_LENGTH) : String  {
        val ny = substring(0, min(max, length))
        if (ny.length != length)
               return ny.plus(" (").plus(length - max).plus(" more chars...)")
        return ny
    }

    fun String.asBearer() = "Bearer ".plus(this)
    fun String.limit(bytes: ByteArray?, max: Int = DEFAULT_LENGTH) = Arrays.toString(bytes).limit(max)
    fun String.mask(mask: String = "*") = replace(("[^\\.]").toRegex(), mask)
}