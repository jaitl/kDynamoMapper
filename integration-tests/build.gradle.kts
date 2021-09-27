dependencies {
    implementation(project(":lib"))

    implementation("ch.qos.logback:logback-core:1.2.6")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("org.slf4j:slf4j-api:1.7.32")

    testImplementation("org.testcontainers:testcontainers:1.16.0")
}
