package com.osama.pro.app.di

import com.osama.pro.core.domain.interactor.DetailInteractor
import com.osama.pro.core.domain.interactor.DetailUseCase
import com.osama.pro.core.domain.interactor.MovieInteractor
import com.osama.pro.core.domain.interactor.MovieUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {
    @Binds
    @ViewModelScoped
    abstract fun bindsMovieUseCase(movieInteractor: MovieInteractor): MovieUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindsDetailUseCase(detailInteractor: DetailInteractor): DetailUseCase
}