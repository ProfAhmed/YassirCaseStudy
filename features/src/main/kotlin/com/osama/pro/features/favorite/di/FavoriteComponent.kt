package com.osama.pro.features.favorite.di

import android.content.Context
import com.osama.pro.app.di.FavoriteModuleDependencies
import com.osama.pro.features.favorite.ui.movie.MovieFragment
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [FavoriteModuleDependencies::class])
interface FavoriteComponent {
    fun inject(fragment: MovieFragment)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(favoriteModuleDependencies: FavoriteModuleDependencies): Builder
        fun build(): FavoriteComponent
    }
}
