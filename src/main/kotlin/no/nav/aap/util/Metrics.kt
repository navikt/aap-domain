package no.nav.aap.util

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Metrics.globalRegistry

object Metrikker {
    fun inc(navn: String, vararg tags: String) = Counter.builder(navn)
        .tags(*tags.map(String::lowercase).toTypedArray())
        .register(globalRegistry)
        .increment()
}