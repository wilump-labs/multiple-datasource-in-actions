package labs.wilump.datasource.common.support

import labs.wilump.datasource.logger
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InitializeValidator(
    private val applicationContext: ApplicationContext,
) {
    private val log = logger()

    @Bean
    fun validateOpenInViewInterceptor() = ApplicationRunner {
        val beanNames = applicationContext.beanDefinitionNames

        if (beanNames.contains("openInViewInterceptor")) {
            log.warn("OpenInViewInterceptor is enabled. This can cause performance issues. Please consider disabling it.")
        }
    }
}