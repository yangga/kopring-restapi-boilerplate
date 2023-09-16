package com.yangga.kopringrestapiboilerplate.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
@EnableConfigurationProperties(LoggingWebFilterConfiguration::class)
class LoggingConfig {
    @Bean @Primary
    fun loggingFilterConfiguration(): LoggingWebFilterConfiguration {
        return LoggingWebFilterConfiguration()
    }
}

@ConfigurationProperties(prefix = "spring.logging-filter", ignoreUnknownFields = true)
data class LoggingWebFilterConfiguration(
    var enabled: Boolean = false,
    var ignorePattern: String = "/(actuator(/\\w+)+|health|font|static|csv(/\\w+)+|.*(swagger|csrf|api-docs).*|.+\\.(ico|js|css|png|gif|jsp))",
    var request: LoggingRequest = LoggingRequest(),
    var response: LoggingResponse = LoggingResponse(),
) {
    data class LoggingRequest(
        var enabled: Boolean = false,
        var body: LoggingRequestBody = LoggingRequestBody(),
    )

    data class LoggingRequestBody(
        var enabled: Boolean = false,
    )

    data class LoggingResponse(
        var enabled: Boolean = false,
        var body: LoggingResponseBody = LoggingResponseBody(),
    )

    data class LoggingResponseBody(
        var enabled: Boolean = false,
    )
}

