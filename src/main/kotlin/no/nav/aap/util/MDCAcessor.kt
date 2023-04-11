package no.nav.aap.util

import io.micrometer.context.ContextRegistry
import io.micrometer.context.ThreadLocalAccessor
import org.slf4j.MDC
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder.*
import reactor.core.publisher.Hooks

class MDCAccessor : ThreadLocalAccessor<Map<String, String>> {

    override fun key() = KEY

    override fun getValue() = MDC.getCopyOfContextMap() ?: emptyMap()

    override fun setValue(map : Map<String, String>) = MDC.setContextMap(map)

    override fun reset() = MDC.clear()

    companion object {

        private const val KEY = "mdc"
    }
}

class RequestAttributesAccessor : ThreadLocalAccessor<RequestAttributes> {

    override fun key() = RequestAttributesAccessor::class.java.name

    override fun getValue()  = getRequestAttributes()

    override fun setValue(attributes : RequestAttributes) =
        setRequestAttributes(attributes)

    override fun reset() = resetRequestAttributes()
}

object AccessorUtil {

    fun init() = run {
        Hooks.enableAutomaticContextPropagation()
        ContextRegistry.getInstance().registerThreadLocalAccessor(MDCAccessor())
    }
}