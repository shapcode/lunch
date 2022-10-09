package com.shapcode.lunch.views.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shapcode.lunch.databinding.FragmentRestaurantDetailBinding
import com.shapcode.lunch.shared.vm.RestaurantDetailViewModel
import com.shapcode.lunch.shared.vm.RestaurantDetailViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantDetailFragment : Fragment() {

    private val viewModel: RestaurantDetailViewModel by viewModels()
    private var _viewBinding: FragmentRestaurantDetailBinding? = null
    private val viewBinding: FragmentRestaurantDetailBinding
        get() = _viewBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewBinding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.viewState.collect {
                    renderViewState(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun renderViewState(viewState: RestaurantDetailViewState) {
        viewState.restaurant?.let {
            viewBinding.name.text = viewState.restaurant.name
            viewBinding.phone.text = viewState.restaurant.phone
            viewBinding.address.text = viewState.restaurant.address
            viewState.restaurant.website?.let { url ->
                viewBinding.website.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            }?: run { viewBinding.website.visibility = View.GONE }

            viewBinding.share.setOnClickListener {
                startActivity(
                    ShareCompat.IntentBuilder(requireContext())
                        .setType("text/plain")
                        .setText(viewState.restaurant.address)
                        .createChooserIntent()
                )
            }

        }
    }

}