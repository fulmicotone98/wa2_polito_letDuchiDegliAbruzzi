package wa2.polito.it.letduchidegliabruzzi.server.observability

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class AbstractLogAspect {

    @Throws(Throwable::class)
    open fun logInfoAround(joinPoint: ProceedingJoinPoint): Any? {
        val logInfo = getLogInfo(joinPoint)
        val log: Logger = LoggerFactory.getLogger(logInfo.declaringType)
        logBefore(logInfo, log)

        val result: Any? = joinPoint.proceed()

        logAfter(logInfo, log)
        return result
    }

    private fun getLogInfo(joinPoint: ProceedingJoinPoint): LogInfo {
        val signature: Signature = joinPoint.signature
        val declaringType: Class<*> = signature.declaringType
        val className: String = declaringType.simpleName
        val annotatedMethodName: String = signature.name
        val args: Array<Any?> = joinPoint.args
        return LogInfo(declaringType, className, annotatedMethodName, args)
    }

    private fun logBefore(logInfo: LogInfo, log: Logger) {
        log.info("[{}.{}] start ({})", logInfo.className, logInfo.annotatedMethodName, logInfo.args)
    }

    private fun logAfter(logInfo: LogInfo, log: Logger) {
        log.info("[{}.{}] end", logInfo.className, logInfo.annotatedMethodName)
    }

    fun logBefore(joinPoint: ProceedingJoinPoint) {
        val logInfo = getLogInfo(joinPoint)
        val log: Logger = LoggerFactory.getLogger(logInfo.declaringType)
        logBefore(logInfo, log)
    }

    fun logAfter(joinPoint: ProceedingJoinPoint) {
        val logInfo = getLogInfo(joinPoint)
        val log: Logger = LoggerFactory.getLogger(logInfo.declaringType)
        logAfter(logInfo, log)
    }

    private data class LogInfo(
        @field:NotNull val declaringType: Class<*>,
        @field:NotNull val className: String,
        @field:NotNull val annotatedMethodName: String,
        @field:Nullable val args: Array<Any?>?
    )
}
