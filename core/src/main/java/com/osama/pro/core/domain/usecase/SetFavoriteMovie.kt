package com.osama.pro.core.domain.usecase

import com.osama.pro.core.domain.model.Movie
import com.osama.pro.core.domain.repository.Repository
import javax.inject.Inject

class SetFavoriteMovie @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(movie: Movie, isFavorite: Boolean) = repository.setFavorite(movie, isFavorite)
}