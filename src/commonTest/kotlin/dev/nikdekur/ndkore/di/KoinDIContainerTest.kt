package dev.nikdekur.ndkore.di

import dev.nikdekur.ndkore.koin.SimpleKoinContext

class KoinDIContainerTest : DIContainerTest() {
    override fun createContainer(): DIContainer {
        return KoinDIContainer {
            val context = SimpleKoinContext()
            context.startKoin { }
            context(context)
        }
    }
}