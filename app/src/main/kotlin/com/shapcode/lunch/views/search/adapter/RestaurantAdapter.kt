package com.shapcode.lunch.views.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.shapcode.lunch.R
import com.shapcode.lunch.core.model.Restaurant

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

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo = itemView.findViewById<ImageView>(R.id.photo)
        val name = itemView.findViewById<TextView>(R.id.name)
        val rating = itemView.findViewById<RatingBar>(R.id.rating)
        val reviews = itemView.findViewById<TextView>(R.id.reviews)
        val priceLevel = itemView.findViewById<TextView>(R.id.price_level)
        val favorite = itemView.findViewById<CheckBox>(R.id.favorite)
    }

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
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val context = holder.itemView.context

        val item = items[position]
        holder.photo.load(item.photoUrl)
        holder.name.text = item.name
        holder.reviews.text = context.getString(R.string.reviews, item.reviews)
        holder.priceLevel.text = context.getString(R.string.price_level,
            "$".repeat(item.priceLevel),
            context.getString(priceLevelDescriptions[item.priceLevel])
        )
        holder.rating.rating = item.rating.toFloat()
        holder.favorite.setOnCheckedChangeListener(null)
        holder.favorite.isChecked = favorites.contains(item.id)
        holder.favorite.setOnCheckedChangeListener { _, isChecked ->
            onToggleFavoriteListener(item, isChecked)
        }
        holder.itemView.setOnClickListener {
            onItemClickListener(item)
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
