package com.shapcode.lunch.views.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shapcode.lunch.R
import com.shapcode.lunch.views.search.adapter.RestaurantAdapter
import kotlinx.coroutines.launch
import com.shapcode.lunch.shared.vm.RestaurantSearchViewModel

class RestaurantListFragment : Fragment() {

    private val viewModel: RestaurantSearchViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.results_list)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.itemAnimator = null
        val adapter = RestaurantAdapter(
            onItemClickListener = {
                findNavController().navigate(
                    R.id.action_restaurantSearchFragment_to_restaurantDetailFragment,
                    bundleOf("restaurantId" to it.id)
                )
            },
            onToggleFavoriteListener = { restaurant, isFavorite ->
                viewModel.setFavorite(restaurant.id, isFavorite)
            }
        )
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.viewState.collect { viewState ->
                    adapter.setItems(viewState.restaurants, viewState.favorites)
                }
            }
        }

    }

}

