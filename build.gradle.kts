import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.google.cloud.tools.jib") version "3.3.1"
	kotlin("jvm") version "1.9.22"
	kotlin("kapt") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
	id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
}
group = "com.starskvim"
version = "0.0.1-SNAPSHOT"
jib.to.image = "archive-app"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}

}

repositories {
	mavenCentral()
}

dependencies {
	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	// Spring
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-aop:3.2.0")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.2.0")
//	implementation("org.springdoc:springdoc-openapi-ui:2.3.0")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")
//	implementation("org.springdoc:springdoc-openapi-kotlin:2.3.0")
	//
	implementation("commons-io:commons-io:2.11.0")
	implementation("org.apache.commons:commons-collections4:4.4")
	implementation("commons-codec:commons-codec:1.15")
	implementation("io.minio:minio:8.2.2")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")
}

kapt {
	arguments {
		arg("mapstruct.defaultComponentModel", "spring")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}