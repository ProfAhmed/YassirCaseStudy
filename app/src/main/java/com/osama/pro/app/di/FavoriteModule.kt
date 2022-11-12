package com.osama.pro.app.di

import com.osama.pro.core.domain.interactor.FavoriteInteractor
import com.osama.pro.core.domain.interactor.FavoriteUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoriteModule {
    @Binds
    abstract fun bindsFavoriteUseCase(favoriteInteractor: FavoriteInteractor): FavoriteUseCase
}