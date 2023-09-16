package com.yangga.kopringrestapiboilerplate.support.spec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.Logging
import org.jooq.impl.DSL
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import reactor.core.publisher.Flux
import java.nio.charset.StandardCharsets
import java.nio.file.Files


abstract class BehaviorSpecWithContainer(body: BehaviorSpec.() -> Unit) : Logging, BehaviorSpec({
    extension(SpringExtension)

    isolationMode = IsolationMode.InstancePerLeaf

    body()
}) {

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)

        // initialize data for testing
        val resource = ClassPathResource("sql/data-test.sql")
        val sqlScript = String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8)

        val connectionFactory = ConnectionFactories.get(
            ConnectionFactoryOptions
                .parse("r2dbc:mariadb://${container!!.host}:${container!!.firstMappedPort}/${DATABASE_NAME}")
                .mutate()
                .option(ConnectionFactoryOptions.USER, DATABASE_USER)
                .option(ConnectionFactoryOptions.PASSWORD, DATABASE_PASSWORD)
                .build()
        )

        withContext(Dispatchers.IO) {
            val queries = sqlScript.split(";").stream()
                .map(String::trim)
                .filter(String::isNotEmpty)
                .map(DSL::query)
                .toList()

            Flux.from(DSL.using(connectionFactory).batch(queries)).blockLast()
        }
    }

    companion object {
        private val DATABASE_NAME = "sample"
        private val DATABASE_USER = "root"
        private val DATABASE_PASSWORD = "root"

        private var container: GenericContainer<*>? = null

        private fun initContainer() {
            container = GenericContainer("mariadb:10")
                .apply {
                    addEnv("MYSQL_DATABASE", DATABASE_NAME)
                    addEnv("MYSQL_ROOT_PASSWORD", DATABASE_PASSWORD)
                    addExposedPort(3306)
                    waitingFor(Wait.forListeningPorts(3306))
                    withReuse(false)
                    start()
                }
        }

        private fun destroyContainer() {
            if (container != null) {
                container!!.stop()
                container = null
            }
        }

        @JvmStatic
        @DynamicPropertySource
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            if (container != null) {
                destroyContainer()
            }
            initContainer()

            val host = container!!.host
            val port = container!!.firstMappedPort

            // update properties
            registry.add("spring.persistence.sample.host") { "${host}:${port}" }
            registry.add("spring.persistence.sample.database") { DATABASE_NAME }
            registry.add("spring.persistence.sample.user") { DATABASE_USER }
            registry.add("spring.persistence.sample.password") { DATABASE_PASSWORD }
        }
    }
}
