package com.osama.pro.app.di

import com.osama.pro.app.utils.AppPreferences
import com.osama.pro.core.domain.interactor.FavoriteUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FavoriteModuleDependencies {
    fun provideFavoriteUseCase(): FavoriteUseCase
    fun provideAppPreferences(): AppPreferences
}