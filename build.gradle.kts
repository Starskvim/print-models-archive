import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
	id("com.google.cloud.tools.jib") version "3.3.1"
	kotlin("jvm") version "1.7.22"
	kotlin("kapt") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
}

group = "com.starskvim"
version = "0.0.1-SNAPSHOT"
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
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
	implementation("org.mapstruct:mapstruct:1.5.4.Final")
	implementation("commons-io:commons-io:2.11.0")
	implementation("org.springframework.boot:spring-boot-starter-aop:3.0.5")
	implementation("org.apache.commons:commons-collections4:4.4")
	implementation("commons-codec:commons-codec:1.15")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("io.minio:minio:8.2.2")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
	kapt("org.mapstruct:mapstruct-processor:1.5.4.Final")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
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