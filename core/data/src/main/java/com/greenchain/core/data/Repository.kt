package com.greenchain.core.data

interface Repository {
    suspend fun hello(): String
}
