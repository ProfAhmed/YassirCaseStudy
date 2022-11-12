package com.osama.pro.core.data.source.remote.network

import com.osama.pro.core.data.source.remote.response.ListResponse
import com.osama.pro.core.data.source.remote.response.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieServices {
    @GET("search/movie")
    suspend fun searchMovie(
        @Query("query")
        query: String,
        @Query("page")
        page: Int = 1,
    ): ListResponse<MovieResponse>

    @GET("discover/movie")
    suspend fun getMovies(
        @Query("page")
        page: Int = 1,
    ): ListResponse<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id")
        id: Int,
        @Query("append_to_response")
        extra: String = com.osama.pro.core.data.Constants.appendToResponse,
    ): MovieResponse

}