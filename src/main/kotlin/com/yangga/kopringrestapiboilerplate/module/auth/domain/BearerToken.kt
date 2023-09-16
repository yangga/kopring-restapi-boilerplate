package com.yangga.kopringrestapiboilerplate.module.auth.domain

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils

class BearerToken(val value: String) : AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
    companion object {}

    override fun getCredentials() = value
    override fun getPrincipal() = value
}