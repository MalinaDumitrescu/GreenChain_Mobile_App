package com.greenchain.core.data.di

import com.greenchain.core.data.Repository
import com.greenchain.core.data.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindRepository(impl: RepositoryImpl): Repository
}
