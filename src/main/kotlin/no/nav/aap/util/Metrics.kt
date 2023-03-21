package no.nav.aap.util

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Metrics.globalRegistry

object Metrikker {

    fun inc(navn: String, vararg tags: Pair<String,Any>) =
        tags(Counter.builder(navn),*tags)
            .register(globalRegistry)
            .increment()

    fun inc(navn: String, vararg tags: String) = Counter.builder(navn)
        .tags(*tags.map(String::lowercase).toTypedArray())
        .register(globalRegistry)
        .increment()

    private fun tags(b: Counter.Builder, vararg pairs: Pair<String,Any>) =  pairs.iterator().forEachRemaining { tag(b, it) }.let { b }

    private fun tag(b: Counter.Builder, t: Pair<String,Any>) = b.tags(t.first,"${t.second}")

}