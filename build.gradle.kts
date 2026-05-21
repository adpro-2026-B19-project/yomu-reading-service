plugins {
    java
    jacoco
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"
description = "yomu-reading-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21) 
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "id/ac/ui/cs/advprog/yomureadingservice/config/**",
                    "id/ac/ui/cs/advprog/yomureadingservice/YomuReadingServiceApplication**",
                    "id/ac/ui/cs/advprog/yomureadingservice/**/dto/**",
                    "id/ac/ui/cs/advprog/yomureadingservice/**/model/**",
                    "id/ac/ui/cs/advprog/yomureadingservice/client/**"
                )
            }
        })
    )
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "id/ac/ui/cs/advprog/yomureadingservice/config/**",
                    "id/ac/ui/cs/advprog/yomureadingservice/YomuReadingServiceApplication**",
                    "id/ac/ui/cs/advprog/yomureadingservice/**/dto/**",
                    "id/ac/ui/cs/advprog/yomureadingservice/**/model/**",
                    "id/ac/ui/cs/advprog/yomureadingservice/client/**"
                )
            }
        })
    )
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}