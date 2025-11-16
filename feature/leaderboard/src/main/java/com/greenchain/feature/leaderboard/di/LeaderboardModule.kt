package com.greenchain.feature.leaderboard.di

import com.google.firebase.firestore.FirebaseFirestore
import com.greenchain.feature.leaderboard.data.LeaderboardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LeaderboardModule {

    @Provides
    fun provideLeaderboardRepository(firestore: FirebaseFirestore): LeaderboardRepository {
        return LeaderboardRepository(firestore)
    }
}
