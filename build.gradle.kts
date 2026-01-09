@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.licenser)
    alias(libs.plugins.kotlinSerialization)
    id("maven-publish")
}

group = "dev.nikdekur"
version = "1.6.5"

val authorId: String by project
val authorName: String by project

kotlin {
    explicitApi()
    jvm {
        compilerOptions {
            freeCompilerArgs.addAll("-Xno-param-assertions", "-Xno-call-assertions", "-Xno-receiver-assertions")
            jvmTarget = JvmTarget.JVM_1_8
        }
    }

    // iOS
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

    // Desktop
    // mingwX64()
    // linuxX64()
    // linuxArm64()
    // macosX64()
    // macosArm64()

    // Web
    js {
        outputModuleName = project.name
        browser()
        nodejs()
    }

    wasmJs {
        outputModuleName = project.name + "Wasm"
        browser()
        nodejs()
    }

    sourceSets {

        commonMain.dependencies {

            // Kotlin require both api and compileOnly for some targets
            val dependencies = listOf(
                libs.kotlinx.coroutines.core,
                libs.kotlinx.serialization.core,
                libs.kotlinx.serialization.json,
                libs.kotlinx.serialization.properties,
                libs.kotlinx.datetime,
                libs.kotlinx.io.core,
                libs.kotlin.logging,
                libs.stately.concurrency,
                libs.stately.concurrent.collections,
                libs.bignum,
                libs.kaml,
                libs.koin,
                kotlin("test")
            ).forEach {
                compileOnly(it)
                api(it)
            }
        }

        jvmMain.dependencies {
            compileOnly(libs.slf4j.api)
            compileOnly(libs.google.guava)
            compileOnly(libs.lz4)
        }

        commonTest.dependencies {
            implementation(libs.slf4j.api)

            implementation(libs.kotlinx.coroutines.test)
            implementation(kotlin("test"))

            // Logback is not supported on jdk-8
            implementation(libs.slf4j.simple)
            implementation(libs.koin)
            implementation(libs.google.guava)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.properties)
            implementation(libs.stately.concurrent.collections)
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


license {
    header(project.file("HEADER"))
    properties {
        set("year", "2025-present")
        set("name", authorName)
    }
}


val repoUsernameProp = "NDK_REPO_USERNAME"
val repoPasswordProp = "NDK_REPO_PASSWORD"
val repoUsername: String? = System.getenv(repoUsernameProp)
val repoPassword: String? = System.getenv(repoPasswordProp)

if (repoUsername.isNullOrBlank() || repoPassword.isNullOrBlank()) {
    throw GradleException("Environment variables $repoUsernameProp and $repoPasswordProp must be set.")
}

publishing {

    repositories {
        maven {
            name = "ndk-repo"
            url = uri("https://repo.nikdekur.uk/releases")
            credentials {
                username = repoUsername
                password = repoPassword
            }
        }

        mavenLocal()
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                developers {
                    developer {
                        id.set(authorId)
                        name.set(authorName)
                    }
                }
            }

            from(components["kotlin"])
        }
    }
}