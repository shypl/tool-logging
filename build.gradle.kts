plugins {
	kotlin("jvm") version "1.9.20"
	id("java-library")
	id("maven-publish")
	id("nebula.release") version "17.2.2"
}

group = "org.shypl.tool"

kotlin {
	jvmToolchain(17)
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.slf4j:slf4j-api:2.0.7")
	
	testImplementation(kotlin("test"))
	testImplementation("ch.qos.logback:logback-classic:1.4.9")
}

java {
	withSourcesJar()
}

publishing {
	publications.create<MavenPublication>("Library") {
		from(components["java"])
	}
}