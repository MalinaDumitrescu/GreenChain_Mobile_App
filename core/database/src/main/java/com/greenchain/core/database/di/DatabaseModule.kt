//package com.greenchain.core.database.di
//
//import android.content.Context
//import androidx.room.Room
//import com.greenchain.core.database.AppDatabase
//import com.greenchain.core.database.StubDao
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object DatabaseModule {
//
//    @Provides @Singleton
//    fun provideDb(@ApplicationContext context: Context): AppDatabase =
//        Room.databaseBuilder(context, AppDatabase::class.java, "greenchain.db").build()
//
//    @Provides
//    fun provideStubDao(db: AppDatabase): StubDao = db.stubDao()
//}
