package com.shapcode.lunch.views.search

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shapcode.lunch.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.shapcode.lunch.shared.vm.RestaurantSearchViewModel

@AndroidEntryPoint
class RestaurantSearchFragment : Fragment() {

    private val viewModel: RestaurantSearchViewModel by viewModels()

    private lateinit var viewTypeButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurant_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val queryInput = view.findViewById<EditText>(R.id.query_input)
        queryInput.addTextChangedListener {
            viewModel.searchRestaurants(it?.toString() ?: "")
        }
        viewTypeButton = view.findViewById(R.id.toggle_view_type)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.viewType.collect { updateViewType(it) }
            }
        }
    }

    private fun updateViewType(viewType: RestaurantSearchViewModel.ViewType) {
        when (viewType) {
            RestaurantSearchViewModel.ViewType.LIST -> {
                if (childFragmentManager.findFragmentById(R.id.results) !is RestaurantListFragment) {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.results, RestaurantListFragment())
                        .commit()
                }
                viewTypeButton.text = getString(R.string.map)
                viewTypeButton.setDrawableStart(
                    ContextCompat.getDrawable(requireContext(), R.drawable.baseline_location_on_24)
                )
                viewTypeButton.setOnClickListener {
                    viewModel.setViewType(RestaurantSearchViewModel.ViewType.MAP)
                }
            }
            RestaurantSearchViewModel.ViewType.MAP -> {
                if (childFragmentManager.findFragmentById(R.id.results) !is RestaurantMapFragment) {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.results, RestaurantMapFragment())
                        .commit()
                }
                viewTypeButton.text = getString(R.string.list)
                viewTypeButton.setDrawableStart(
                    ContextCompat.getDrawable(requireContext(), R.drawable.baseline_list_24)
                )
                viewTypeButton.setOnClickListener {
                    viewModel.setViewType(RestaurantSearchViewModel.ViewType.LIST)
                }
            }
        }
    }

}

fun TextView.setDrawableStart(drawable: Drawable?) =
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        drawable,
        compoundDrawables[1],
        compoundDrawables[2],
        compoundDrawables[3]
    )