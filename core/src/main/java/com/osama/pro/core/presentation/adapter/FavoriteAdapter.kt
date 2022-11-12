package com.osama.pro.core.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osama.pro.core.databinding.ItemRowLayoutBinding
import com.osama.pro.core.domain.model.DomainModel
import com.osama.pro.core.domain.model.Movie
import com.osama.pro.core.presentation.viewholder.CardViewHolder
import com.osama.pro.core.utils.ImageSize

class FavoriteAdapter<T : DomainModel>(
    private val imageSize: ImageSize = ImageSize.Medium,
    private val onItemClick: (item: T) -> Unit,
) : RecyclerView.Adapter<CardViewHolder>() {
    private val list = ArrayList<T>()

    fun submitData(newList: List<T>) {
        with(list) {
            clear()
            addAll(newList)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder =
        CardViewHolder(
            ItemRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = list[position] as Movie
        val callback = onItemClick as (item: Movie) -> Unit
        holder.bind(item, imageSize, callback)
    }

    override fun getItemCount(): Int = list.size
}