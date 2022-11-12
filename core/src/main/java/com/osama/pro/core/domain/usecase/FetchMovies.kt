package com.osama.pro.core.domain.usecase

import com.osama.pro.core.domain.repository.Repository
import javax.inject.Inject

class FetchMovies @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke() = repository.fetchMovies()
}