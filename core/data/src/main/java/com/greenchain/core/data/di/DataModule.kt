package com.greenchain.core.data.di

import com.greenchain.core.data.Repository
import com.greenchain.core.data.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository
}
