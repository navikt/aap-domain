package no.nav.aap.rest

import no.nav.aap.util.CallIdGenerator
import no.nav.aap.util.MDCUtil
import java.io.IOException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class HeadersToMDCFilter(val applicationName: String) : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        putValues(HttpServletRequest::class.java.cast(request))
        chain.doFilter(request, response)
    }

    private fun putValues(req: HttpServletRequest) {
        MDCUtil.toMDC(MDCUtil.NAV_CONSUMER_ID, req.getHeader(MDCUtil.NAV_CONSUMER_ID), applicationName)
        MDCUtil.toMDC(MDCUtil.NAV_CALL_ID, req.getHeader(MDCUtil.NAV_CALL_ID), CallIdGenerator.create())
    }
    override fun toString() = javaClass.simpleName + " [ applicationName=" + applicationName + "]"
}