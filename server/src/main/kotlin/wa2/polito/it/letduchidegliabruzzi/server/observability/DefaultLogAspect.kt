package wa2.polito.it.letduchidegliabruzzi.server.observability

import org.aspectj.lang.ProceedingJoinPoint

import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect


@Aspect
class DefaultLogAspect : AbstractLogAspect() {
    @Throws(Throwable::class)
    override fun logInfoAround(joinPoint: ProceedingJoinPoint): Any? {
        return super.logInfoAround(joinPoint)
    }
}