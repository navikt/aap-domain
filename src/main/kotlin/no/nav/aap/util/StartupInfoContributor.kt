package no.nav.aap.util

import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.context.ApplicationContext

class StartupInfoContributor(private val ctx: ApplicationContext) : InfoContributor {
    override fun contribute(builder: Info.Builder) {
        builder.withDetail(
                "extra-info", mapOf(
                "Startup time" to TimeUtil.format(ctx.startupDate)))
    }
}