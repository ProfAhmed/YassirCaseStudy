package com.osama.pro.core.domain.usecase

import com.osama.pro.core.domain.repository.Repository
import javax.inject.Inject

class SearchMovie @Inject constructor(
    private val repository: Repository,
) {
    operator fun invoke(query: String) = repository.searchMovie(query)
}