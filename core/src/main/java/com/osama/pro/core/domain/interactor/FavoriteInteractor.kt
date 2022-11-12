package com.osama.pro.core.domain.interactor

import com.osama.pro.core.domain.usecase.GetFavoriteMovies
import javax.inject.Inject

data class FavoriteInteractor @Inject constructor(
    override val getFavoriteMovies: GetFavoriteMovies,
) : FavoriteUseCase

interface FavoriteUseCase {
    val getFavoriteMovies: GetFavoriteMovies
}