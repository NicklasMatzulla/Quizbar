/*
 * Copyright 2023 Nicklas Matzulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

buildscript {
	repositories {
		mavenCentral()
		maven { setUrl("https://maven.vaadin.com/vaadin-prereleases") }
	}
}

plugins {
	id("java")
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
	id("com.vaadin") version "24.1.8"
}

group = "de.nicklasmatzulla"
version = "1.0.0-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	developmentOnly
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
	runtimeOnly {
		extendsFrom(developmentOnly.get())
	}
}

repositories {
	mavenCentral()
	maven { setUrl("https://maven.vaadin.com/vaadin-prereleases") }
	maven { setUrl("https://maven.vaadin.com/vaadin-addons") }
}

extra["vaadinVersion"] = "24.1.8"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.vaadin:vaadin-spring-boot-starter")
	implementation("org.parttio:line-awesome:1.1.0")
	implementation("com.google.code.gson:gson:2.10.1")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mariadb")
}

dependencyManagement {
	imports {
		mavenBom("com.vaadin:vaadin-bom:${property("vaadinVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
