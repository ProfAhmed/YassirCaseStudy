package com.osama.pro.app.home.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.osama.pro.app.state.UiState
import com.osama.pro.core.domain.interactor.MovieUseCase
import com.osama.pro.core.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieUseCase: MovieUseCase,
) : ViewModel() {
    val movieAction = MutableStateFlow<MovieAction?>(null)
    private val _state = MutableStateFlow<UiState<PagingData<Movie>>>(UiState.Idle)
    val state: StateFlow<UiState<PagingData<Movie>>>
        get() = _state.asStateFlow()
    val query = MutableStateFlow(String())
    private var previousIntent: MovieAction? = null
    private fun handleIntent() {
        viewModelScope.launch {
            movieAction.collect { intent ->
                previousIntent?.let { }
                when (intent) {
                    MovieAction.FetchMovie -> fetchMovie()
                    MovieAction.SearchMovie -> searchMovie()
                }
            }
        }
    }

    init {
        handleIntent()
        viewModelScope.launch {
            movieAction.value = MovieAction.FetchMovie
        }
    }

    private fun fetchMovie() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            movieUseCase.fetchMovies()
                .cachedIn(viewModelScope)
                .collectLatest {
                    _state.value = UiState.Success(it)
                }
        }
    }

    private fun searchMovie() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            query.flatMapLatest { movieUseCase.searchMovie(it) }
                .cachedIn(viewModelScope)
                .collectLatest {
                    _state.value = UiState.Success(it)
                }
        }
    }
}