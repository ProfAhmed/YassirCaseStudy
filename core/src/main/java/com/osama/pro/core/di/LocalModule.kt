package com.osama.pro.core.di

import android.app.Application
import androidx.room.Room
import com.osama.pro.core.BuildConfig
import com.osama.pro.core.data.Constants.DATABASE_NAME
import com.osama.pro.core.data.source.local.MovieDatabase
import com.osama.pro.core.data.source.local.dao.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Singleton
    @Provides
    fun provideDatabase(application: Application): MovieDatabase =
        Room
            .databaseBuilder(application, MovieDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .openHelperFactory(SupportFactory(SQLiteDatabase.getBytes(BuildConfig.PARAPHRASE.toCharArray())))
            .build()

    @Provides
    fun provideMovieDao(database: MovieDatabase): MovieDao = database.movieDao()
}