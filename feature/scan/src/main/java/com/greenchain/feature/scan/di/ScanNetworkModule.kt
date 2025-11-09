package com.greenchain.feature.scan.di

import com.greenchain.feature.scan.BuildConfig
import com.greenchain.feature.scan.data.RoboFlowApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RoboFlowModelId

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RoboFlowApiKey



@Module
@InstallIn(SingletonComponent::class)
object ScanNetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val headers = Interceptor { chain ->
            val req = chain.request()
                .newBuilder()
                .header("Accept", "application/json")
                .build()
            chain.proceed(req)
        }

        return OkHttpClient.Builder()
            .addInterceptor(headers)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://detect.roboflow.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRoboFlowApi(retrofit: Retrofit): RoboFlowApi =
        retrofit.create(RoboFlowApi::class.java)

    @Provides
    @Singleton
    @RoboFlowModelId
    fun provideRoboFlowModelId(): String = BuildConfig.ROBOFLOW_MODEL_ID

    @Provides
    @Singleton
    @RoboFlowApiKey
    fun provideRoboFlowApiKey(): String = BuildConfig.ROBOFLOW_API_KEY
}
