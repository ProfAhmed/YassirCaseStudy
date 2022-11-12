package com.osama.pro.app.detail

import com.osama.pro.core.domain.model.DomainModel

sealed class DetailAction {
    object FetchDetails : DetailAction()
    data class FavoriteStateChanged(val model: DomainModel) : DetailAction()
    object Empty : DetailAction()
}