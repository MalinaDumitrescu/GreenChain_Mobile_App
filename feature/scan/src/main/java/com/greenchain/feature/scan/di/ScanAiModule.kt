package com.greenchain.feature.scan.ai

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScanAiModule {

    @Provides
    @Singleton
    fun provideSgrLogoDetector(
        @ApplicationContext context: Context
    ): SgrLogoDetector = SgrLogoDetector(context)
}
