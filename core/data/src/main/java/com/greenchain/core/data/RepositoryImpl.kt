package com.greenchain.core.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor() : Repository {
    override suspend fun hello(): String {
        // For now, we'll just return a simple string.
        // Later, you would inject your DAOs or network services here to fetch real data.
        return "Hello from RepositoryImpl!"
    }
}
