package com.keypadds.phonechallenge.core.di

import com.keypadds.phonechallenge.data.repository.RecentSongRepositoryImpl
import com.keypadds.phonechallenge.data.repository.SongRepositoryImpl
import com.keypadds.phonechallenge.domain.repository.RecentSongRepository
import com.keypadds.phonechallenge.domain.repository.SongRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSongRepository(impl: SongRepositoryImpl): SongRepository

    @Binds
    @Singleton
    abstract fun bindRecentSongRepository(impl: RecentSongRepositoryImpl): RecentSongRepository
}
