package com.yangga.kopringrestapiboilerplate.common.config

import com.yangga.kopringrestapiboilerplate.common.error.enums.ErrorCode
import com.yangga.kopringrestapiboilerplate.common.payload.ApiResponse
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod

@Configuration
@Profile("local", "dev", "qa", "stage")
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .addSecurityItem(SecurityRequirement().addList("Bearer Authentication"))
        .components(Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
        .addServersItem(Server().url("/"))
        .info(apiInfo())

    private fun apiInfo() = Info()
        .title("API Introduction")
        .description("Hello world~!")
        .version("0.0.1")

    private fun createAPIKeyScheme(): SecurityScheme {
        return SecurityScheme().type(SecurityScheme.Type.HTTP)
            .bearerFormat("JWT")
            .scheme("bearer")
    }
}

@Component
@Profile("local", "dev", "qa", "stage")
class OpenApiOperationCustomizer : OperationCustomizer {
    override fun customize(operation: Operation, handlerMethod: HandlerMethod?): Operation {
        val parameterHeader: Parameter = Parameter()
            .name("x-transaction-id")
            .`in`(ParameterIn.HEADER.toString())
            .schema(StringSchema())
            .description("Transaction ID")
        operation.addParametersItem(parameterHeader)

        val schemaError = ModelConverters.getInstance().resolveAsResolvedSchema(AnnotatedType(ApiResponse.Error::class.java)).schema

        operation.responses(
            operation.responses.addApiResponse("400", io.swagger.v3.oas.models.responses.ApiResponse().description("Bad Request").content(
                Content().addMediaType(
                    MediaType.APPLICATION_JSON_VALUE, io.swagger.v3.oas.models.media.MediaType().schema(schemaError.description("Error"))
                    .addExamples("default", Example().value(ApiResponse.error(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST.name)).description("sample error").summary("sample"))
                )
            )),
        )

        return operation
    }
}