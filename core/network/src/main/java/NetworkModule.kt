//package com.greenchain.core.network.di
//
//import com.greenchain.core.network.ApiService
//import com.google.gson.Gson
//import com.google.gson.GsonBuilder
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import javax.inject.Singleton
//
//private const val BASE_URL = "https://example.com/api/" // TODO change to your backend
//
//@Module
//@InstallIn(SingletonComponent::class)
//object NetworkModule {
//
//    @Provides @Singleton
//    fun provideLogging(): HttpLoggingInterceptor =
//        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
//
//    @Provides @Singleton
//    fun provideOkHttp(logging: HttpLoggingInterceptor): OkHttpClient =
//        OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .build()
//
//    @Provides @Singleton
//    fun provideGson(): Gson = GsonBuilder().create()
//
//    @Provides @Singleton
//    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//
//    @Provides @Singleton
//    fun provideApi(retrofit: Retrofit): ApiService =
//        retrofit.create(ApiService::class.java)
//}
