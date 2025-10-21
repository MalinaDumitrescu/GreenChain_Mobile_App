// app/src/main/java/com/greenchain/di/CoreModule.kt
package com.greenchain.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

interface AppInfoProvider {
    fun welcome(): String
}

private class DefaultAppInfoProvider : AppInfoProvider {
    override fun welcome() = "GreenChain ready 🌱"
}

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Provides @Singleton
    fun provideAppInfoProvider(): AppInfoProvider = DefaultAppInfoProvider()
}
