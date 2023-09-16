package com.yangga.kopringrestapiboilerplate.module.user.adapter.out

import com.yangga.kopringrestapiboilerplate.database.sample.tables.references.USERS
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitLast
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class UserRepositoryDSL(private val dsl: DSLContext) {
    suspend fun add(userEntity: UserEntity): Int = Mono.from(
        dsl.insertInto(USERS, USERS.EMAIL, USERS.PASSWORD, USERS.USERNAME)
            .values(userEntity.email, userEntity.password, userEntity.username)
    ).awaitFirst()

    suspend fun findAll(): List<UserEntity> = Flux.from(dsl.selectFrom(USERS))
        .map { it.into(UserEntity::class.java) }
        .collectList()
        .awaitLast()
}