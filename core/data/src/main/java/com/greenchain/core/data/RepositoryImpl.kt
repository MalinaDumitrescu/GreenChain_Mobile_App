package com.greenchain.core.data

import com.greenchain.core.database.StubDao
import com.greenchain.core.database.StubEntity
import com.greenchain.core.network.ApiService
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dao: StubDao
) : Repository {

    override suspend fun hello(): String {
        dao.get()?.let { return "DB: ${it.message}" }
        val dto = runCatching { api.ping() }.getOrNull()
        val msg = dto?.message ?: "Hello (offline)"
        dao.insert(StubEntity(message = msg))
        return if (dto != null) "API: $msg" else "DB: $msg"
    }
}
