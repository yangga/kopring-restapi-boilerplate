import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("org.openapi.generator") version "7.0.0" // !!openapi-generator
    id("nu.studer.jooq") version "8.2"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "com.yangga"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

object DependencyVersions {
    const val JAKARTA_SERVLET_API_VERSION="6.0.0"
    const val JJWT_VERSION = "0.11.5"   // !!jjwt
    const val JOOQ_VERSION = "3.18.6"
    const val KOTEST_EXTENSION_VERSION = "1.1.3"
    const val KOTEST_VERSION = "5.6.2"
    const val LOG4J_API_VERSION = "1.3.0-SNAPSHOT"
    const val MARIADB_DRIVER_VERSION = "1.1.4"
    const val MOCKITO_VERSION = "4.6.1"
    const val MOCKK_VERSION = "1.13.7"
    const val R2DBC_POOL_VERSION = "1.0.1.RELEASE"
    const val SENTRY_VERSION = "6.28.0" // !!sentry
    const val SPRINGDOC_VERSION = "2.2.0"
    const val SPRING_MOCKK_VERSION = "4.0.2"
    const val TESTCONTAINERS_VERSION = "1.19.0"
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    configureEach {
        exclude(group="org.springframework.boot", module="spring-boot-starter-logging")
    }
}

repositories {
    mavenCentral()
    maven { setUrl("https://repository.apache.org/snapshots") }
}

dependencies {
    // !!r2dbc + jooq
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-pool:${DependencyVersions.R2DBC_POOL_VERSION}")
    implementation("org.mariadb:r2dbc-mariadb:${DependencyVersions.MARIADB_DRIVER_VERSION}")   // https://r2dbc.io/drivers/
    implementation("org.jooq:jooq:${DependencyVersions.JOOQ_VERSION}")
    compileOnly("org.jooq:jooq-meta:${DependencyVersions.JOOQ_VERSION}")
    compileOnly("org.jooq:jooq-codegen:${DependencyVersions.JOOQ_VERSION}")
    compileOnly("org.mariadb.jdbc:mariadb-java-client")
    jooqGenerator("org.mariadb.jdbc:mariadb-java-client")
    testImplementation("org.testcontainers:testcontainers:${DependencyVersions.TESTCONTAINERS_VERSION}")
    testImplementation("org.testcontainers:r2dbc:${DependencyVersions.TESTCONTAINERS_VERSION}")
    testImplementation("org.testcontainers:mariadb:${DependencyVersions.TESTCONTAINERS_VERSION}")

    // !!jjwt
    implementation("io.jsonwebtoken:jjwt-api:${DependencyVersions.JJWT_VERSION}")
    implementation("io.jsonwebtoken:jjwt-impl:${DependencyVersions.JJWT_VERSION}")
    implementation("io.jsonwebtoken:jjwt-jackson:${DependencyVersions.JJWT_VERSION}")
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // !!sentry
    implementation("io.sentry:sentry-spring-boot-starter-jakarta:${DependencyVersions.SENTRY_VERSION}")
    implementation("io.sentry:sentry-log4j2:${DependencyVersions.SENTRY_VERSION}")

    implementation("org.apache.logging.log4j:log4j-layout-template-json")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:${DependencyVersions.LOG4J_API_VERSION}")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${DependencyVersions.SPRINGDOC_VERSION}")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")    // for mac m1
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
    testImplementation("io.mockk:mockk:${DependencyVersions.MOCKK_VERSION}")
    testImplementation("com.ninja-squad:springmockk:${DependencyVersions.SPRING_MOCKK_VERSION}")
    testImplementation("io.kotest:kotest-runner-junit5:${DependencyVersions.KOTEST_VERSION}")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:${DependencyVersions.KOTEST_EXTENSION_VERSION}")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("jakarta.servlet:jakarta.servlet-api:${DependencyVersions.JAKARTA_SERVLET_API_VERSION}")
}

configurations.forEach {
    it.exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    it.exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
}

sourceSets {
    main {
        kotlin {
            srcDir("$buildDir/generated/src/main/kotlin")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// !!openapi-generator >>>
val openApiMap = mapOf(
    "com.swagger.petstore3" to "https://petstore3.swagger.io/api/v3/openapi.json"
)

openApiMap.keys.forEach { domain ->
    tasks.withType<GenerateTask> {
        generatorName = "kotlin"    // https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/kotlin.md
        remoteInputSpec = openApiMap[domain]
        outputDir = "$buildDir/generated"
        apiPackage = "$domain.api"
        modelPackage = "$domain.model"
        configOptions = mapOf(
            "library" to "jvm-spring-webclient",
            "serializationLibrary" to "jackson",
            "dateLibrary" to "java8",
            "useSpringBoot3" to "true"
        )
    }
}
// <<< !!openapi-generator

// !!jooq generation >>>
jooq {
    version = DependencyVersions.JOOQ_VERSION
    edition = nu.studer.gradle.jooq.JooqEdition.OSS

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation = true

            jooqConfiguration.apply {

                logging = org.jooq.meta.jaxb.Logging.WARN
                onError = org.jooq.meta.jaxb.OnError.LOG

                jdbc.apply {
                    driver = "org.mariadb.jdbc.Driver"
                    url = "jdbc:mariadb://localhost:3306/sample"
                    user = "root"
                    password = "root"
                    properties.add(org.jooq.meta.jaxb.Property().apply {
                        key = "ssl"
                        value = "true"
                    })
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    strategy.apply {
                        name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    }
                    database.apply {
                        // https://www.jooq.org/doc/3.15/manual/code-generation/codegen-advanced/codegen-config-database/codegen-database-name/
                        name = "org.jooq.meta.mariadb.MariaDBDatabase"
                        inputSchema = "sample" // db name

                        includes = ".*" // regex
                        excludes = "test_.* | temp_.*"
                    }
                    generate.apply {
                        generatedSerialVersionUID = org.jooq.meta.jaxb.GeneratedSerialVersionUID.CONSTANT
                        isJavaTimeTypes = true    // java.time.*

                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "com.yangga.kopringrestapiboilerplate.database.sample"
                        directory = "$buildDir/generated/src/main/kotlin"
                    }
                }
            }
        }
    }
}
// <<< !!jooq generation

tasks.compileKotlin {
    dependsOn("generateJooq")
    dependsOn("openApiGenerate")
}

apply(from = "$projectDir/gradle/prePush.gradle")