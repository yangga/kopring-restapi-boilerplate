package com.yangga.kopringrestapiboilerplate.persistence.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.persistence", ignoreInvalidFields = true)
class PersistenceProperties {
    var sample: Map<String, String>? = null
}
