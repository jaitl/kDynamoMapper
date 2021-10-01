plugins {
    `java-library`
    `maven-publish`
    jacoco
    signing
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

val releaseVersion: String? by project

val sonatypeUsername: String? by project
val sonatypePassword: String? by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
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

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "pro.jaitl"
            artifactId = "k-dynamo-mapper"
            version = releaseVersion ?: "SNAPSHOT"
            pom {
                name.set("kDynamoMapper")
                description.set("Lightweight AWS DynamoDB mapper for Kotlin.")
                url.set("https://github.com/jaitl/kDynamoMapper")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/jaitl/kDynamoMapper/blob/main/LICENSE")
                    }
                }
                organization {
                    name.set("jaitl")
                    url.set("https://github.com/jaitl")
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/jaitl/kDynamoMapper/issues")
                }
                developers {
                    developer {
                        id.set("jaitl")
                        name.set("Igor Rize")
                        email.set("jaitl@outlook.com")
                    }
                }
                scm {
                    url.set("https://github.com/jaitl/kDynamoMapper")
                    connection.set("scm:https://github.com/jaitl/kDynamoMapper.git")
                    developerConnection.set("scm:https://github.com/jaitl/kDynamoMapper.git")
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
