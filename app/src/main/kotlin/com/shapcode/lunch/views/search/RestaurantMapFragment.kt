package com.shapcode.lunch.views.search

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.Coil
import coil.request.ImageRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.shapcode.lunch.R
import com.shapcode.lunch.core.model.Restaurant
import kotlinx.coroutines.launch
import com.shapcode.lunch.shared.vm.RestaurantSearchViewModel

class RestaurantMapFragment : Fragment() {

    private val viewModel: RestaurantSearchViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private lateinit var mapFragment: SupportMapFragment
    var map: GoogleMap? = null

    var restaurantMap: Map<String, Restaurant> = emptyMap()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurant_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            mapFragment = childFragmentManager.findFragmentById(R.id.results_map) as SupportMapFragment
        } else {
            mapFragment = SupportMapFragment.newInstance(GoogleMapOptions()
                .camera(CameraPosition.fromLatLngZoom(
                    LatLng(
                        viewModel.location?.lat ?: 0.0,
                        viewModel.location?.lng ?: 0.0
                    ), 11f
                ))
            )
            childFragmentManager.beginTransaction()
                .add(R.id.results_map, mapFragment)
                .commit()
        }
        mapFragment.getMapAsync { map ->
            this.map = map
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark))
            }
            map.setOnInfoWindowClickListener {
                findNavController().navigate(
                    R.id.action_restaurantSearchFragment_to_restaurantDetailFragment,
                    bundleOf("restaurantId" to it.snippet)
                )
            }
            observeViewState()
        }
    }

    private fun observeViewState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.viewState.collect { viewState ->
                    map?.let { map ->
                        map.clear()
                        restaurantMap = buildMap {
                            viewState.restaurants.forEach { restaurant ->
                                val marker = map.addMarker(MarkerOptions()
                                    .snippet(restaurant.id)
                                    .title(restaurant.name)
                                    .position(LatLng(
                                        restaurant.location.lat,
                                        restaurant.location.lng
                                    ))
                                )
                                if (marker != null) {
                                    put(restaurant.id, restaurant)
                                }
                            }
                        }
                        map.setInfoWindowAdapter(InfoWindowAdapter(requireContext(), restaurantMap))
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                LatLngBounds(
                                    LatLng(
                                        viewState.restaurants.minBy { it.location.lat }.location.lat,
                                        viewState.restaurants.minBy { it.location.lng }.location.lng
                                    ),
                                    LatLng(
                                        viewState.restaurants.maxBy { it.location.lat }.location.lat,
                                        viewState.restaurants.maxBy { it.location.lng }.location.lng
                                    )
                                ), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32f, resources.displayMetrics).toInt()
                            )
                        )
                    }
                }
            }
        }

    }

    inner class InfoWindowAdapter(
        private val context: Context,
        private val restaurantMarkerMap: Map<String, Restaurant>
    ): GoogleMap.InfoWindowAdapter {

        private val priceLevelDescriptions = listOf(
            R.string.price_level_description_0,
            R.string.price_level_description_1,
            R.string.price_level_description_2,
            R.string.price_level_description_3,
            R.string.price_level_description_4,
        )

        private inner class VH(
            val itemView: View
        ) {
            val photo = itemView.findViewById<ImageView>(R.id.photo)
            val name = itemView.findViewById<TextView>(R.id.name)
            val rating = itemView.findViewById<RatingBar>(R.id.rating)
            val reviews = itemView.findViewById<TextView>(R.id.reviews)
            val priceLevel = itemView.findViewById<TextView>(R.id.price_level)
        }

        private val viewHolder = VH(LayoutInflater.from(context).inflate(R.layout.item_restaurant, null, false).also {
            it.findViewById<View>(R.id.favorite).visibility = View.GONE
        })

        var lastId: String? = null

        override fun getInfoContents(marker: Marker): View? {
            return null
        }

        override fun getInfoWindow(marker: Marker): View? {
            restaurantMarkerMap[marker.snippet]?.let {
                viewHolder.name.text = it.name
                viewHolder.reviews.text = context.getString(R.string.reviews, it.reviews)
                viewHolder.priceLevel.text = context.getString(R.string.price_level,
                    "$".repeat(it.priceLevel),
                    context.getString(priceLevelDescriptions[it.priceLevel])
                )
                viewHolder.rating.rating = it.rating.toFloat()
                if (lastId != marker.snippet) {
                    viewHolder.photo.setImageDrawable(null)
                    lastId = marker.snippet
                }
                if (viewHolder.photo.drawable == null) {
                    lifecycleScope.launch {
                        Coil.imageLoader(requireContext())
                            .execute(ImageRequest.Builder(requireContext())
                                .data(it.photoUrl)
                                .allowHardware(false)
                                .lifecycle(viewLifecycleOwner)
                                .target { drawable ->
                                    viewHolder.photo.setImageDrawable(drawable)
                                    marker.hideInfoWindow()
                                    marker.showInfoWindow()
                                }
                                .build()
                            )
                    }
                }
            }

            return viewHolder.itemView
        }
    }

}
