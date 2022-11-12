package com.osama.pro.core.presentation.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.osama.pro.core.databinding.ItemRowLayoutBinding
import com.osama.pro.core.domain.model.Movie
import com.osama.pro.core.extension.glideImageWithOptions
import com.osama.pro.core.utils.ImageSize

class CardViewHolder(private val binding: ItemRowLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(movie: Movie, imageSize: ImageSize, onItemClick: (movie: Movie) -> Unit) =
        with(binding) {
            tvTitle.text = movie.title
            imgPoster.glideImageWithOptions(movie.getPosterUrl(imageSize))

            cvItem.setOnClickListener { onItemClick(movie) }
            ratingBar.rating = movie.voteAverage.toFloat().div(2)
            tvRating.text = movie.voteAverage.toString()
            tvDate.text = movie.releaseDate
        }
}