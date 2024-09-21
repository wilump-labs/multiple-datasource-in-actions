package labs.wilump.datasource.banking

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
    basePackages = ["labs.wilump.datasource.banking"],
    entityManagerFactoryRef = "bankingEntityManagerFactory",
    transactionManagerRef = "bankingTransactionManager"
)
class BankingDataSourceConfig(
    private val jpaConfig: JpaConfig,
) {
    @Primary
    @Bean(name = ["bankingDataSourceProperties"])
    @ConfigurationProperties(prefix = "spring.datasource.banking")
    fun bankingDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Primary
    @Bean(name = ["bankingDataSource"])
    @ConfigurationProperties(prefix = "spring.datasource.banking.hikari")
    fun bankingDataSource(): DataSource {
        return bankingDataSourceProperties().initializeDataSourceBuilder()
            .type(HikariDataSource::class.java).build()
    }

    @Primary
    @Bean(name = ["bankingEntityManagerFactory"])
    fun bankingEntityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = bankingDataSource()
        em.setPackagesToScan("labs.wilump.datasource.banking")
        em.persistenceUnitName = "banking"
        em.jpaVendorAdapter = HibernateJpaVendorAdapter()
        em.setJpaProperties(jpaConfig.jpaProperties())
        return em
    }

    @Primary
    @Bean(name = ["bankingTransactionManager"])
    fun bankingTransactionManager(): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = bankingEntityManagerFactory().getObject() as EntityManagerFactory
        return transactionManager
    }

    @Primary
    @Bean(name = ["bankingJdbcTemplate"])
    fun bankingJdbcTemplate(): JdbcTemplate {
        return JdbcTemplate(bankingDataSource())
    }
}