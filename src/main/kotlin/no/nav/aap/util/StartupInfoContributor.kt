package no.nav.aap.util

import no.nav.aap.util.TimeExtensions.format
import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.context.ApplicationContext

class StartupInfoContributor(private val ctx: ApplicationContext) : InfoContributor {
    override fun contribute(builder: Info.Builder) {
        builder.withDetail(
                "extra-info", mapOf(
                "Startup time" to ctx.startupDate.format()))
    }
}