package com.osama.pro.features.favorite.ui.movie

import android.content.Context
import com.osama.pro.app.di.FavoriteModuleDependencies
import com.osama.pro.core.domain.model.Movie
import com.osama.pro.features.favorite.di.DaggerFavoriteComponent
import com.osama.pro.features.favorite.presentation.fragment.BaseFavoriteFragment
import dagger.hilt.android.EntryPointAccessors

class MovieFragment : BaseFavoriteFragment<Movie>() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerFavoriteComponent.builder()
            .context(context)
            .appDependencies(EntryPointAccessors.fromApplication(
                context.applicationContext,
                FavoriteModuleDependencies::class.java
            ))
            .build()
            .inject(this)
    }

    override fun loadData() {
        viewModel.favoriteMovies.observe(viewLifecycleOwner, ::render)
    }
}