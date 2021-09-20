import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm") version "1.5.30"
    jacoco
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "jacoco")

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
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

        testImplementation("org.jetbrains.kotlin:kotlin-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
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

    tasks {
        test {
            testLogging {
                events("failed")
                showStackTraces = true
                exceptionFormat = TestExceptionFormat.FULL
            }
        }
    }
}