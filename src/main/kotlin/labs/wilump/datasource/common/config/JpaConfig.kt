package labs.wilump.datasource.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@EnableJpaAuditing
@Configuration
class JpaConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.jpa")
    fun springJpaProperties(): Properties {
        return Properties()
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.jpa.properties")
    fun springJpaHibernateProperties(): Properties {
        return Properties()
    }

    @Bean
    fun jpaProperties(): Properties {
        val jpaProps = springJpaProperties()

        // set hibernate.hbm2ddl.auto from spring.jpa.hibernate.ddl-auto
        if (jpaProps.getProperty("hibernate.ddl-auto") != null) {
            jpaProps.setProperty(
                "hibernate.hbm2ddl.auto",
                jpaProps.getProperty("hibernate.ddl-auto")
            )
        }

        // set hibernate properties from spring.jpa.properties
        val hibernateProps = springJpaHibernateProperties()
        jpaProps.putAll(hibernateProps)

        return jpaProps
    }
}