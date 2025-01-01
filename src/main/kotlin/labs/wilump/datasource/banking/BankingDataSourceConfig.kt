package labs.wilump.datasource.banking

import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import labs.wilump.datasource.common.config.DynamicRoutingDataSource
import labs.wilump.datasource.common.config.DynamicRoutingDataSource.Companion.READER
import labs.wilump.datasource.common.config.DynamicRoutingDataSource.Companion.WRITER
import labs.wilump.datasource.common.config.JpaConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.*
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
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
    @Bean(name = ["bankingDataSourceProperties"])
    @ConfigurationProperties(prefix = "spring.datasource.banking")
    fun bankingDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean(name = [WRITER_DATA_SOURCE])
    @ConfigurationProperties("spring.datasource.banking.hikari")
    fun bankingDataSource(): DataSource {
        return bankingDataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean(name = ["bankingReaderDataSourceProperties"])
    @ConfigurationProperties(prefix = "spring.datasource.banking-reader")
    fun bankingReaderDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean(name = [READER_DATA_SOURCE])
    @ConfigurationProperties("spring.datasource.banking-reader.hikari")
    fun bankingReaderDataSource(): DataSource {
        return bankingReaderDataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean
    @DependsOn(WRITER_DATA_SOURCE, READER_DATA_SOURCE)
    fun routingDataSource(
        @Qualifier(WRITER_DATA_SOURCE) writerDataSource: DataSource,
        @Qualifier(READER_DATA_SOURCE) readerDataSource: DataSource,
    ): DataSource {
        val routingDataSource = DynamicRoutingDataSource()
        val dataSourceMap: MutableMap<Any, Any> = HashMap()
        dataSourceMap[WRITER] = writerDataSource
        dataSourceMap[READER] = readerDataSource
        routingDataSource.setTargetDataSources(dataSourceMap)
        routingDataSource.setDefaultTargetDataSource(writerDataSource)
        return routingDataSource
    }

    @Bean
    @Primary
    @DependsOn("routingDataSource")
    fun dataSource(
        @Qualifier("routingDataSource") routingDataSource: DataSource,
    ): DataSource {
        return LazyConnectionDataSourceProxy(routingDataSource)
    }

    @Bean(name = ["bankingEntityManagerFactory"])
    fun bankingEntityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.setPackagesToScan("com.banking.usecase.domain.core")
        em.jpaVendorAdapter = HibernateJpaVendorAdapter()
        em.setJpaProperties(jpaConfig.jpaProperties())
        return em
    }

    @Bean(name = ["bankingTransactionManager"])
    fun bankingTransactionManager(
        @Qualifier("bankingEntityManagerFactory") bankingEntityManagerFactory: LocalContainerEntityManagerFactoryBean,
    ): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = bankingEntityManagerFactory.getObject()
        return transactionManager
    }

    @Bean(name = ["bankingJdbcTemplate"])
    fun bankingJdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }

    companion object {
        private const val WRITER_DATA_SOURCE = "bankingDataSource"
        private const val READER_DATA_SOURCE = "bankingReaderDataSource"
    }
}