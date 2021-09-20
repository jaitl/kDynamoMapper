plugins {
    kotlin("jvm") version "1.5.30"
    jacoco
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions { jvmTarget = "11" } }
}

tasks.compileJava {
    options.release.set(11)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("software.amazon.awssdk:dynamodb:2.17.37")

    implementation("ch.qos.logback:logback-core:1.2.6")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("org.slf4j:slf4j-api:1.7.32")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    testImplementation("org.testcontainers:testcontainers:1.16.0")
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}
