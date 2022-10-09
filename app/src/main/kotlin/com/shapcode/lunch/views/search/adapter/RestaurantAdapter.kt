package com.shapcode.lunch.views.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.shapcode.lunch.R
import com.shapcode.lunch.core.model.Restaurant
import com.shapcode.lunch.databinding.ItemRestaurantBinding

class RestaurantAdapter(
    private val onItemClickListener: (Restaurant) -> Unit = {},
    private val onToggleFavoriteListener: (Restaurant, Boolean) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<RestaurantAdapter.VH>() {

    private val priceLevelDescriptions = listOf(
        R.string.price_level_description_0,
        R.string.price_level_description_1,
        R.string.price_level_description_2,
        R.string.price_level_description_3,
        R.string.price_level_description_4,
    )

    class VH(val viewBinding: ItemRestaurantBinding) : RecyclerView.ViewHolder(viewBinding.root)

    private var items: List<Restaurant> = listOf()
    private var favorites: Set<String> = setOf()

    fun setItems(items: List<Restaurant>, favorites: Set<String> = setOf()) {
        val result = DiffUtil.calculateDiff(
            DiffCalculator(
                this.items, items,
                this.favorites, favorites
            )
        )
        this.items = items
        this.favorites = favorites
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        with(holder) {
            val context = itemView.context

            val item = items[position]
            viewBinding.photo.load(item.photoUrl)
            viewBinding.name.text = item.name
            viewBinding.reviews.text = context.getString(R.string.reviews, item.reviews)
            viewBinding.priceLevel.text = context.getString(R.string.price_level,
                "$".repeat(item.priceLevel),
                context.getString(priceLevelDescriptions[item.priceLevel])
            )
            viewBinding.rating.rating = item.rating.toFloat()
            viewBinding.favorite.setOnCheckedChangeListener(null)
            viewBinding.favorite.isChecked = favorites.contains(item.id)
            viewBinding.favorite.setOnCheckedChangeListener { _, isChecked ->
                onToggleFavoriteListener(item, isChecked)
            }
            holder.itemView.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private class DiffCalculator(
        val oldList: List<Restaurant>,
        val newList: List<Restaurant>,
        val oldFavorites: Set<String>,
        val newFavorites: Set<String>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList.getOrNull(oldItemPosition) == newList.getOrNull(newItemPosition)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItemId = oldList.getOrNull(oldItemPosition)?.id
            val newItemId = newList.getOrNull(newItemPosition)?.id
            val oldItemFavorite = oldFavorites.contains(oldItemId)
            val newItemFavorite = newFavorites.contains(newItemId)
            return oldItemId == newItemId && oldItemFavorite == newItemFavorite
        }
    }

}
