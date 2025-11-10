package com.greenchain.feature.map.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.greenchain.feature.map.data.RecyclingPointDataSource
import com.greenchain.feature.map.data.RecyclingPointRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapModule {

    @Provides
    @Singleton
    fun provideRecyclingPointDataSource(@ApplicationContext context: Context): RecyclingPointDataSource {
        return RecyclingPointDataSource(context)
    }

    @Provides
    @Singleton
    fun provideRecyclingPointRepository(dataSource: RecyclingPointDataSource): RecyclingPointRepository {
        return RecyclingPointRepository(dataSource)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}
