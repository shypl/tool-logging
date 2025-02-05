plugins {
	kotlin("jvm") version "2.1.10"
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
	implementation("org.slf4j:slf4j-api:2.0.16")
	
	testImplementation(kotlin("test"))
	testImplementation("ch.qos.logback:logback-classic:1.5.16")
}

java {
	withSourcesJar()
}

publishing {
	publications.create<MavenPublication>("Library") {
		from(components["java"])
	}
	repositories.maven("https://maven.pkg.github.com/shypl/packages").credentials {
		username = project.property("shypl.gpr.user") as String
		password = project.property("shypl.gpr.key") as String
	}
}

tasks.release {
	finalizedBy(tasks.publish)
}