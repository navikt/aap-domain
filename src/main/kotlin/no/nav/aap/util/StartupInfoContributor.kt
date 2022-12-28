package no.nav.aap.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import no.nav.aap.util.TimeExtensions.format
import org.springframework.boot.actuate.info.Info.Builder
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.context.ApplicationContext

class StartupInfoContributor(private val ctx: ApplicationContext) : InfoContributor {
    override fun contribute(builder: Builder) {
        builder.withDetail("extra-info", mapOf("Startup time" to ctx.startupDate.local()))
    }

    private fun Long.local(fmt: String = "yyyy-MM-dd HH:mm:ss") =  LocalDateTime.ofInstant(Instant.ofEpochMilli(this),
            ZoneId.of("Europe/Oslo")).format(DateTimeFormatter.ofPattern(fmt))

}