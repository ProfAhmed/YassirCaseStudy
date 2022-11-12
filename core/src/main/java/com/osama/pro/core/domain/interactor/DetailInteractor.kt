package com.osama.pro.core.domain.interactor

import com.osama.pro.core.domain.usecase.GetMovieDetails
import com.osama.pro.core.domain.usecase.SetFavoriteMovie
import javax.inject.Inject


data class DetailInteractor @Inject constructor(
    override val getMovieDetails: GetMovieDetails,
    override val setFavoriteMovie: SetFavoriteMovie,

    ) : DetailUseCase

interface DetailUseCase {
    val getMovieDetails: GetMovieDetails
    val setFavoriteMovie: SetFavoriteMovie

}