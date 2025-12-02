plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.7"

	// Plugin JaCoCo para geração de relatório de cobertura
	id("jacoco")
}

// config para JaCoCo
jacoco {
	toolVersion = "0.8.11"
}

group = "br.greeeen"
version = "0.0.1-SNAPSHOT"
description = "Backend Baronesa Petshop E-commerce"

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
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("com.google.firebase:firebase-admin:9.2.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

	// Dependência para Unit Tests Mocking
	testImplementation("io.mockk:mockk:1.13.11")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	mainClass.set("br.greeeen.baronesa_petshop_backend.BaronesaPetshopBackendApplicationKt")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Configura a tarefa de relatório de teste
tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}
