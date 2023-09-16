package com.yangga.kopringrestapiboilerplate.persistence.sample.config

import com.yangga.kopringrestapiboilerplate.persistence.properties.PersistenceProperties
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import io.r2dbc.spi.Option
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement


/**
 * Database configuration. This configuration is only used when the profile is not "test".
 */
@Configuration
@EnableR2dbcRepositories
@EnableTransactionManagement
class SampleDatabaseConfig(
    private val properties: PersistenceProperties,
) : AbstractR2dbcConfiguration() {
    @Bean
    fun dslContext(): DSLContext {
        return DSL.using(connectionFactory())
    }

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val options = properties.sample!!.toMutableMap()
            .apply {
                set(DRIVER.name(), "pool")
                set(PROTOCOL.name(), "mariadb")
            }.let { map ->
                builder().apply {
                    map.keys.forEach {
                        val key = Option.valueOf<Any>(it)
                        val value = map[it]
                        if (value != null) {
                            option(key, value)
                        }
                    }
                }.build()
            }

        return ConnectionFactories.get(options)
    }


    @Bean
    fun databaseInitializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer =
        ConnectionFactoryInitializer().apply {
            setConnectionFactory(connectionFactory)
            setDatabasePopulator(
                CompositeDatabasePopulator().apply {
                    addPopulators(
                        ResourceDatabasePopulator(
                            ClassPathResource("sql/01.schema.sql"),
                            ClassPathResource("sql/02.data.sql")
                        )
                    )
                }
            )
        }

    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }
}
