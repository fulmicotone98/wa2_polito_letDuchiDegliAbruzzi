package wa2.polito.it.letduchidegliabruzzi.server.observability

import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.aop.ObservedAspect
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class ObserveConfiguration {

    @Bean
    @ConditionalOnMissingBean(AbstractObserveAroundMethodHandler::class)
    fun observeAroundMethodHandler(): AbstractObserveAroundMethodHandler {
        return DefaultObserveAroundMethodHandler()
    }

    @Bean
    @ConditionalOnMissingBean(ObservedAspect::class)
    fun observedAspect(observationRegistry: ObservationRegistry): ObservedAspect {
        return ObservedAspect(observationRegistry)
    }
}
