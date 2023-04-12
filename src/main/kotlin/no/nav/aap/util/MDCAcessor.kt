package no.nav.aap.util

import io.micrometer.context.ContextRegistry
import io.micrometer.context.ThreadLocalAccessor
import org.slf4j.MDC
import org.slf4j.MDC.*
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder.getRequestAttributes
import org.springframework.web.context.request.RequestContextHolder.resetRequestAttributes
import org.springframework.web.context.request.RequestContextHolder.setRequestAttributes
import reactor.core.publisher.Hooks.enableAutomaticContextPropagation

class MDCAccessor : ThreadLocalAccessor<Map<String, String>> {

    override fun key() = "mdc"

    override fun getValue() = getCopyOfContextMap() ?: emptyMap()

    override fun setValue(map : Map<String, String>) = setContextMap(map)

    override fun reset() = clear()
}

class RequestAttributesAccessor : ThreadLocalAccessor<RequestAttributes> {

    override fun key() = RequestAttributesAccessor::class.java.name

    override fun getValue()  = getRequestAttributes()

    override fun setValue(attributes : RequestAttributes) = setRequestAttributes(attributes)

    override fun reset() = resetRequestAttributes()
}

object AccessorUtil {

    fun init() = run {
        enableAutomaticContextPropagation()
        ContextRegistry.getInstance().apply {
            registerThreadLocalAccessor(RequestAttributesAccessor())
            registerThreadLocalAccessor(MDCAccessor())
        }
     }
}