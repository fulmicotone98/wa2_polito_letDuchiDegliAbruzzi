package wa2.polito.it.letduchidegliabruzzi.server.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableConfigurationProperties(KeycloakProperties::class, LokiProperties::class)
class WebApplicationConfig : WebMvcConfigurer {

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/notFound").setViewName("forward:/index.html")
    }

    @Bean
    fun containerCustomizer(): WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        return WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
                factory -> factory?.addErrorPages(ErrorPage(HttpStatus.NOT_FOUND, "/notFound"))
        }
    }

    @Bean
    fun keycloakConfiguration(): KeycloakProperties {
        return KeycloakProperties()
    }

    @Bean
    fun lokiConfiguration(): LokiProperties{
        return LokiProperties()
    }
}