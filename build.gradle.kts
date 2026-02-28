plugins {
	kotlin("jvm") version "2.3.10"
	id("java-library")
	id("maven-publish")
	id("nebula.release") version "19.0.10"
}

group = "org.shypl.tool"

kotlin {
	jvmToolchain(21)
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.slf4j:slf4j-api:2.0.17")
	
	testImplementation(kotlin("test"))
	testImplementation("ch.qos.logback:logback-classic:1.5.25")
}

java {
	withSourcesJar()
}

publishing {
	publications.create<MavenPublication>("Library") {
		from(components["java"])
	}
	repositories.maven(project.property("shypl.maven.url") as String).credentials {
		username = project.property("shypl.maven.username") as String
		password = project.property("shypl.maven.password") as String
	}
}

tasks.release {
	finalizedBy(tasks.publish)
}