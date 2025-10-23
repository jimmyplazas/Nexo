package dev.alejo.nexo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform