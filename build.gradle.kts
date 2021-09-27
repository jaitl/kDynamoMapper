import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        implementation("software.amazon.awssdk:dynamodb:2.17.40")

        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
        withType<JavaCompile> {
            sourceCompatibility = "11"
            targetCompatibility = "11"
        }
        test {
            testLogging {
                events("failed")
                showStackTraces = true
                exceptionFormat = TestExceptionFormat.FULL
            }
        }
    }
}
