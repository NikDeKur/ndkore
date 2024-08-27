# ndkore - NDK Core

Utility library based on Kotlin that provides a huge number of utilities
that you might need in development of any project.

## About

### Dependencies

ndkore were planned to be a library that has functionality not only for
kotlin and java standard library, but also for other libraries and frameworks.
ndkore doesn't force you to use all libraries and frameworks that it supports,
so there are no libraries at ndkore jar. If you are using some library
or framework that ndkore supports,
you have to add it to your project dependencies.

### Features

There are short descriptions of some features that ndkore provides,
for more information, please refer to KDocs.

- Services system to manage services
- Placeholder system
- Extension functions for huge number of classes
- Reflection utilities, Class Finder
- Scheduler system for creating tasks
- MultiMap, ListsMap, SetsMap and SpreadMap
- Spatial data structures, ex: Octree, KDTree
- Memory calculation utilities
- Utility interfaces, ex Snowflake
- [Koin](https://insert-koin.io/) extensions and SimpleKoinContext

### Java and Kotlin

As ndkore based on Kotlin, it technically can be used in Java projects,
but some features might not work as expected,
Ex: inline functions, reified types, etc.

## Installation

Replace `{version}` with the latest version number on repository.

### Gradle (Kotlin)

```kotlin
repositories {
    maven("https://repo.nikdekur.tech/releases")
}

dependencies {
    implementation("dev.nikdekur:ndkore:{version}")
}
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url "https://repo.nikdekur.tech/releases" }
}

dependencies {
    implementation "dev.nikdekur:ndkore:{version}"
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
    <version>{version}</version>
</dependency>
```