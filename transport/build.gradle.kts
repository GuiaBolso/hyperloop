dependencies {
    // Projects
    implementation(project(":validator"))

    // AWS
    implementation("com.amazonaws:aws-java-sdk-sqs:${rootProject.extra.get("aws-version")}")
    implementation("com.amazonaws:aws-encryption-sdk-java:2.3.2")

    // Kotson
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    
    // Commons Codec
    implementation("commons-codec:commons-codec:1.15")
}