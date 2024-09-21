package labs.wilump.datasource.membership

import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import labs.wilump.datasource.common.config.JpaConfig
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Profile("!open-in-view-test")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["labs.wilump.datasource.membership"],
    entityManagerFactoryRef = "membershipEntityManagerFactory",
    transactionManagerRef = "membershipTransactionManager"
)
class MembershipDataSourceConfig(
    private val jpaConfig: JpaConfig,
) {
    @Primary
    @Bean(name = ["membershipDataSourceProperties"])
    @ConfigurationProperties(prefix = "spring.datasource.membership")
    fun membershipDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Primary
    @Bean(name = ["membershipDataSource"])
    @ConfigurationProperties(prefix = "spring.datasource.membership.hikari")
    fun membershipDataSource(): DataSource {
        return membershipDataSourceProperties().initializeDataSourceBuilder()
            .type(HikariDataSource::class.java).build()
    }

    @Primary
    @Bean(name = ["membershipEntityManagerFactory"])
    fun membershipEntityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = membershipDataSource()
        em.setPackagesToScan("labs.wilump.datasource.membership")
        em.persistenceUnitName = "membership"
        em.jpaVendorAdapter = HibernateJpaVendorAdapter()
        em.setJpaProperties(jpaConfig.jpaProperties())
        return em
    }

    @Primary
    @Bean(name = ["membershipTransactionManager"])
    fun membershipTransactionManager(): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = membershipEntityManagerFactory().getObject() as EntityManagerFactory
        return transactionManager
    }

    @Primary
    @Bean(name = ["membershipJdbcTemplate"])
    fun membershipJdbcTemplate(): JdbcTemplate {
        return JdbcTemplate(membershipDataSource())
    }
}