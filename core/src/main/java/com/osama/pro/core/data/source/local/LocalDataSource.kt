package com.osama.pro.core.data.source.local

import com.osama.pro.core.data.mapper.mapToDomain
import com.osama.pro.core.data.source.local.dao.MovieDao
import com.osama.pro.core.data.source.local.entity.MovieEntity
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
) {
    fun getFavoriteMovies() = movieDao.getFavoriteMovies()


    fun getFavoriteMovie(id: Int) =
        movieDao.getFavoriteMovie(id)
            .map { it?.mapToDomain() }

    suspend fun addToFavorite(movie: MovieEntity) = movieDao.addToFavorite(movie)

    suspend fun removeFromFavorite(movie: MovieEntity) = movieDao.removeFromFavorite(movie)

}