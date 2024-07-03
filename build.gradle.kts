import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.licenser)
    id("java")
    id("maven-publish")
}

group = "dev.nikdekur"
version = "1.1.0"
val author = "Nik De Kur"

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

val javaVersion = JavaVersion.VERSION_1_8
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
    compileOnly(libs.kotlinx.coroutines)
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.google.guava)
    compileOnly(libs.google.gson)
    compileOnly(libs.slf4j.api)

    testImplementation(kotlin("test"))
}

tasks.named("compileKotlin", KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.addAll("-Xno-param-assertions", "-Xno-call-assertions")
    }
}


license {
    header(project.file("HEADER.txt"))
    properties {
        set("year", "2024")
        set("name", author)
    }
    ignoreFailures = true
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
                        name.set(author)
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