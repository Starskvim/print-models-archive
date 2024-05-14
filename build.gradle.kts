import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.google.cloud.tools.jib") version "3.3.1"
	kotlin("jvm") version "1.9.23"
	kotlin("kapt") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
}
group = "com.starskvim"
version = "0.0.2-SNAPSHOT"
jib.to.image = "archive-app"
java.sourceCompatibility = JavaVersion.VERSION_21

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}

}

repositories {
	mavenLocal()
	mavenCentral()
}

dependencies {
	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
	implementation("org.jetbrains.kotlinx:atomicfu:0.24.0")
	// Spring
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")
	//
	implementation("commons-io:commons-io:2.11.0")
	implementation("org.apache.commons:commons-collections4:4.4")
	implementation("commons-codec:commons-codec:1.15")
	implementation("io.minio:minio:8.2.2")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")
	// experimental infrastructure
	implementation("ru.starskvim:infrastructure-webflux-3-kotlin-autoconfiguration:0.1.2-EXPERIMENTAL")
}

kapt {
	arguments {
		arg("mapstruct.defaultComponentModel", "spring")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}