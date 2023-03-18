package no.nav.aap.util

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
class Metrics(private val registry: MeterRegistry) {
    fun inc(navn: String, vararg tags: String) = Counter.builder(navn)
        .tags(*tags.map(String::lowercase).toTypedArray())
        .register(registry)
        .increment()
}