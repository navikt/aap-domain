package no.nav.aap.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object LoggerUtil {
    fun getSecureLogger(): Logger = LoggerFactory.getLogger("secure")
    fun getLogger(forClass: Class<*>): Logger = LoggerFactory.getLogger(forClass)

     fun kibanaURL() =
        buildString {
            append("<https://logs.adeo.no/s/nav-logs-legacy/app/discover#/")
            append("?_g=(filters:!(),refreshInterval:(pause:!t,value:60000),")
            append("time:(from:now-15m,to:now))")
            append("&_a=(columns:!(level,message,envclass,component,application),")
            append("filters:!(),interval:auto,")
            append("query:(language:kuery,query:%22${MDCUtil.callId()}%22),")
            append("sort:!(!('@timestamp',desc)))|Kibana logs>")
        }
}