dependencies {
    // AWS
    implementation("com.amazonaws:aws-java-sdk-s3:${rootProject.extra.get("aws-version")}")
    
    // Caffeine
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.3")

    // Json Path
    implementation("com.jayway.jsonpath:json-path:2.6.0")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.4")
}