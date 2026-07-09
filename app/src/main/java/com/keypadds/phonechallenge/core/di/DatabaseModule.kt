package com.keypadds.phonechallenge.core.di

import android.content.Context
import androidx.room.Room
import com.keypadds.phonechallenge.data.local.AppDatabase
import com.keypadds.phonechallenge.data.local.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "phone_challenge.db"
        ).fallbackToDestructiveMigration(true).build()

    @Provides
    @Singleton
    fun provideSongDao(database: AppDatabase): SongDao = database.songDao()
}
