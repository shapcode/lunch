package com.shapcode.lunch.views.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shapcode.lunch.R
import com.shapcode.lunch.shared.vm.RestaurantDetailViewModel
import com.shapcode.lunch.shared.vm.RestaurantDetailViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantDetailFragment : Fragment() {

    private val viewModel: RestaurantDetailViewModel by viewModels()

    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var address: TextView
    private lateinit var website: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurant_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        name = view.findViewById(R.id.name)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.viewState.collect {
                    renderViewState(it)
                }
            }
        }
    }

    private fun renderViewState(viewState: RestaurantDetailViewState) {
        viewState.restaurant?.let {
            name.text = viewState.restaurant.name
            phone.text = viewState.restaurant.phone
            address.text = viewState.restaurant.address
            viewState.restaurant.website?.let { url ->
                website.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            }?: run { website.visibility = View.GONE }
        }
    }

}