# ndkore - NDK Core

Utility library based on Kotlin that provides a huge number of utilities
that you might need in development of any project.

ndkore doesn't 100% guarantee that all utilities work as expected,
but most of the utilities covered by tests and are stable.

## About

### Dependencies

ndkore were planned to be a library that has functionality not only for
kotlin and java standard library, but also for other libraries and frameworks.
ndkore doesn't force you to use all libraries and frameworks that it supports,
so there are no libraries at ndkore jar. If you are using some library
or framework that ndkore supports, you have to add it to your project dependencies.

### Features

There are short descriptions of some features that ndkore provides,
for more information, please refer to KDocs.

- Services System to organize your code based on SOLID principles
- Placeholder system for parsing placeholders in strings
- Reflection utilities, Class Finder [JVM only]
- Scheduler system for scheduling tasks
- Event system for creating and handling events
- MultiMap, ListsMap, SetsMap and SpreadMap to simplify your code
- Spatial data structures, ex: Octree, KDTree
- Memory calculation module
- Test utilities, assertions
- Utility interfaces for unified code structure
- [Koin](https://insert-koin.io/) extensions and SimpleKoinContext
- Text API to build independent text components
- Unique ID format - Snowstar (modification of Snowflake)
- Many utility and extension functions for huge number of classes

### Java and Kotlin

Since version 1.3.0, ndkore has migrated to Kotlin Multiplatform, and java support is reduced.
But in some parts, ndkore still supports java, and you can use it in your java project.

## Installation

### Gradle (Kotlin)

```kotlin
repositories {
    maven("https://repo.nikdekur.tech/releases")
}

dependencies {
    implementation("dev.nikdekur:ndkore:1.6.2")
}
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url "https://repo.nikdekur.tech/releases" }
}

dependencies {
    implementation "dev.nikdekur:ndkore:1.6.2"
}
```

### Maven

```xml

<repository>
    <id>ndkore-repo</id>
    <url>https://repo.nikdekur.tech/releases</url>
</repository>
```

---

```xml

<dependency>
    <groupId>dev.nikdekur</groupId>
    <artifactId>ndkore</artifactId>
    <version>1.6.2</version>
</dependency>
```