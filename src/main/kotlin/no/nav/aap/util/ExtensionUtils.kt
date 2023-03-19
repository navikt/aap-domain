package no.nav.aap.util

object ExtensionUtils {
    inline fun <T> T?.whenNull(block: T?.() -> Unit): T? {
        if (this == null) block()
        return this@whenNull
    }
}