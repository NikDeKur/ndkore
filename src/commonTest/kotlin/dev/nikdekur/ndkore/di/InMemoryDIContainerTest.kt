package dev.nikdekur.ndkore.di

class InMemoryDIContainerTest : DIContainerTest() {
    override fun createContainer(): DIContainer {
        return RuntimeDIContainer { }
    }
}