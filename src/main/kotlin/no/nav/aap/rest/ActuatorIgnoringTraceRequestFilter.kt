package no.nav.aap.rest

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.actuate.trace.http.HttpExchangeTracer
import org.springframework.boot.actuate.trace.http.HttpTraceRepository
import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter

class ActuatorIgnoringTraceRequestFilter(repository: HttpTraceRepository?, tracer: HttpExchangeTracer?) :
    HttpTraceFilter(repository, tracer) {
    @Throws(ServletException::class)
    override fun shouldNotFilter(request: HttpServletRequest) =
        request.servletPath.contains("actuator") || request.servletPath.contains("swagger")
}