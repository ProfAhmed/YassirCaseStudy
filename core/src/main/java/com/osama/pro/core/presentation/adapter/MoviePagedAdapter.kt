package com.osama.pro.core.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.osama.pro.core.databinding.ItemRowLayoutBinding
import com.osama.pro.core.domain.model.Movie
import com.osama.pro.core.presentation.utils.MovieDiffCallback
import com.osama.pro.core.presentation.viewholder.CardViewHolder
import com.osama.pro.core.utils.ImageSize

class MoviePagedAdapter(
    private val imageSize: ImageSize,
    private val onItemClick: (movie: Movie) -> Unit,
) : PagingDataAdapter<Movie, CardViewHolder>(movieDiffUtil) {
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        getItem(position)?.let { movie ->
            holder.bind(movie, imageSize, onItemClick)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder =
        CardViewHolder(
            ItemRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    companion object {
        private val movieDiffUtil: MovieDiffCallback = MovieDiffCallback()
    }
}