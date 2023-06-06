package wa2.polito.it.letduchidegliabruzzi.server.observability

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationHandler
import io.micrometer.observation.aop.ObservedAspect
import org.aspectj.lang.ProceedingJoinPoint

open class AbstractObserveAroundMethodHandler : AbstractLogAspect(), ObservationHandler<ObservedAspect.ObservedAspectContext> {
    override fun onStart(context: ObservedAspect.ObservedAspectContext) {
        val joinPoint: ProceedingJoinPoint = context.proceedingJoinPoint
        super.logBefore(joinPoint)
    }

    override fun onStop(context: ObservedAspect.ObservedAspectContext) {
        val joinPoint: ProceedingJoinPoint = context.proceedingJoinPoint
        super.logAfter(joinPoint)
    }

    override fun supportsContext(context: Observation.Context): Boolean {
        return context is ObservedAspect.ObservedAspectContext
    }
}
