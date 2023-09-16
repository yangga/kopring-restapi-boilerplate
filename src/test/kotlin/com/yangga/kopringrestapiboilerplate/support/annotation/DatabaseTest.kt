package com.yangga.kopringrestapiboilerplate.support.annotation

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
annotation class DatabaseTest
