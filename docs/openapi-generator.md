# OpenAPI Generator

We need client code to call the API of another service. [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator) creates this client code.

---

## Table of Contents <!-- omit in toc -->

- [Settings](#settings)
- [Run script](#run-script)

## Settings

### build.gradle.kts

The sample code below generates WebClient sample code using openapi data from 'petstore3.swagger.io'.

```
// !!openapi-generator >>>
val openApiMap = mapOf(
    "org.openapi.example" to "https://petstore3.swagger.io/api/v3/openapi.json"
)

openApiMap.keys.forEach { domain ->
    tasks.withType<GenerateTask> {
        generatorName = "kotlin"    // https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/kotlin.md
        remoteInputSpec = openApiMap[domain]
        outputDir = "$buildDir/openapi-generated"
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
```

## Run script

```bash
./gradlew openApiGenerate
```

