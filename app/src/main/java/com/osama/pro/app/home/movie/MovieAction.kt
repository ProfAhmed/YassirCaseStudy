package com.osama.pro.app.home.movie

sealed class MovieAction {
    object FetchMovie : MovieAction()
    object SearchMovie : MovieAction()
}