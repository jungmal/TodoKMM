package com.test.iliketodo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform