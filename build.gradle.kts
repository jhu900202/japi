plugins {
  java
  id("org.springframework.boot") version "4.0.0-SNAPSHOT"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.japi"
version = "0.0.1-SNAPSHOT"
description = "japi"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(24)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
  maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web"){
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging") // Logback 제외
  }
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-validation")

  implementation("org.springframework.boot:spring-boot-starter-security")

  implementation("org.springframework.boot:spring-boot-starter-log4j2") // Log4j2 사용
  implementation("org.bgee.log4jdbc-log4j2:log4jdbc-log4j2-jdbc4.1:1.16") // SQL 로깅 프록시

  implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
  implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")

  implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta") // 쿼리DSL

  compileOnly("org.projectlombok:lombok")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
  annotationProcessor("jakarta.annotation:jakarta.annotation-api:3.0.0")
  annotationProcessor("jakarta.persistence:jakarta.persistence-api:3.2.0")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
