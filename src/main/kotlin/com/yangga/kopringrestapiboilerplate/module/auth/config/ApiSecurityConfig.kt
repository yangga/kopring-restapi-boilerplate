package com.yangga.kopringrestapiboilerplate.module.auth.config

import com.yangga.kopringrestapiboilerplate.module.user.domain.Authority
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class ApiSecurityConfig {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        authManager: ReactiveAuthenticationManager,
        securityContextRepository: ServerSecurityContextRepository,
    ): SecurityWebFilterChain {
        return http
            .cors { it.disable() }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .logout { it.disable() }
            .authorizeExchange {
                it.pathMatchers(
                    "/",
                    "/health",
                    "/api-docs",
                    "/v?/api-docs/**",
                    "/swagger-resources",
                    "/swagger-resources/**",
                    "/configuration/ui",
                    "/configuration/security",
                    "/swagger-ui.html",
                    "/webjars/**",
                    "/swagger-ui/**",
                    "/v?/test/**",
                    "/v?/auth/register",
                    "/v?/auth/login",
                ).permitAll()
                    .pathMatchers("/v?/admin/**").hasRole(Authority.ADMIN.name)
                    .anyExchange().authenticated()
            }
            .authenticationManager(authManager)
            .securityContextRepository(securityContextRepository)
            .exceptionHandling {
                it.authenticationEntryPoint { swe, _ ->
                    Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED }
                }.accessDeniedHandler { swe, _ ->
                    Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN }
                }
            }
            .build()
    }
}
