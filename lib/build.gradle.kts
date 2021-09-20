plugins {
    `java-library`
    jacoco
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("software.amazon.awssdk:dynamodb:2.17.37")
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
