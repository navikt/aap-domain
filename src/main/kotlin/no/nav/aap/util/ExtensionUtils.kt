package no.nav.aap.util

object ExtensionUtils {

    inline fun <T> T?.whenNull(block : T?.() -> Unit) : T? {
        if (this == null) block()
        return this@whenNull
    }

    inline fun <T, R> Iterable<T>.mapToSet(transform : (T) -> R) : Set<R> {
        return mapTo(HashSet(), transform)
    }
}