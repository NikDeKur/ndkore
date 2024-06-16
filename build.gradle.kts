plugins {
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
}

val kotlin_version: String by project

group = "dev.nikdekur"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    google()

    maven {
        name = "Sonatype Snapshots (Legacy)"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        name = "Sonatype Snapshots"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

val javaVersion = JavaVersion.VERSION_11
java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    // withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.majorVersion))
    }
}

dependencies {
    compileOnly("org.yaml:snakeyaml:2.0")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    compileOnly("com.google.guava:guava:32.1.3-jre")
    compileOnly("com.google.code.gson:gson:2.10.1")
    compileOnly("org.slf4j:slf4j-api:2.0.13")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")


    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                developers {
                    developer {
                        id.set("nikdekur")
                        name.set("Nik De Kur")
                    }
                }
            }

            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}