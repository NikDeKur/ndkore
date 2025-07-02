package dev.nikdekur.ndkore.reflect

class KotlinXReflectMethodTest : ReflectMethodTest() {
    override fun createReflectMethod(): ReflectMethod {
        return KotlinXEncoderReflectMethod()
    }
}