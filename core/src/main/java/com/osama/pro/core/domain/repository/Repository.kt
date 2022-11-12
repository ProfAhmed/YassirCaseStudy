package com.osama.pro.core.domain.repository

import androidx.paging.PagingData
import com.osama.pro.core.data.states.Resource
import com.osama.pro.core.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun fetchMovies(): Flow<PagingData<Movie>>

    fun searchMovie(query: String): Flow<PagingData<Movie>>

    fun fetchMovie(id: Int): Flow<Resource<Movie>>

    fun getFavoriteMovies(): Flow<List<Movie>>

    fun setFavorite(movie: Movie, itemIsFavorite: Boolean)
}