package com.osama.pro.app.detail

import androidx.lifecycle.*
import com.osama.pro.app.R
import com.osama.pro.app.state.UiState
import com.osama.pro.app.utils.Event
import com.osama.pro.core.data.states.Resource
import com.osama.pro.core.domain.interactor.DetailUseCase
import com.osama.pro.core.domain.model.DomainModel
import com.osama.pro.core.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val detailUseCase: DetailUseCase,
) : ViewModel() {
    private val id: MutableStateFlow<Int> = MutableStateFlow(NO_ID)
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>>
        get() = _snackbarText

    private val _favoriteState = MutableStateFlow(false)
    val favoriteState: LiveData<Boolean>
        get() = _favoriteState.asLiveData()

    val action = MutableStateFlow<DetailAction?>(null)
    private val _state = MutableStateFlow<UiState<DomainModel>>(UiState.Idle)
    val state: StateFlow<UiState<DomainModel>>
        get() = _state

    init {
        handle.get<Int>(EXTRA_ID)?.let {
            id.value = it
        }
        
        handleAction()
    }


    private fun handleAction() {
        viewModelScope.launch {
            action.collect { intent ->
                when (intent) {
                    is DetailAction.FavoriteStateChanged -> favoriteStateChanged(intent.model)
                    DetailAction.FetchDetails -> fetchDetails()
                    else -> {}
                }
            }
        }
    }

    private fun fetchDetails() {
        val loadResult = detailUseCase.getMovieDetails(id.value)


        viewModelScope.launch {
            loadResult.collect { resource ->
                _state.value = when (resource) {
                    is Resource.Error -> UiState.Error(resource.exception)
                    is Resource.Loading -> UiState.Loading
                    is Resource.Success -> {
                        _favoriteState.value = resource.dataFromDB()
                        UiState.Success(resource.data)
                    }
                }
            }
        }
    }

    private fun favoriteStateChanged(model: DomainModel) {
        val itemIsFavorite = _favoriteState.value
        _snackbarText.value = if (itemIsFavorite)
            Event(R.string.added_to_my_list)
        else
            Event(R.string.removed_from_my_list)
        when (model) {
            is Movie -> detailUseCase.setFavoriteMovie(model, itemIsFavorite)
        }
    }

    fun toggleFavoriteState(model: DomainModel) {
        _favoriteState.value = !_favoriteState.value
        viewModelScope.launch {
            action.value = DetailAction.FavoriteStateChanged(model)
            delay(100)
            action.value = DetailAction.Empty

        }
    }

    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_ID = "extra_item_id"
        const val NO_ID = -1
    }
}