package com.lacker.staff.features.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lacker.staff.R
import com.lacker.staff.data.dto.restaurant.RestaurantDto
import com.lacker.staff.features.auth.list.RestaurantsListItem
import com.lacker.staff.features.auth.list.getRestaurantSelectingAdapter
import kotlinx.android.synthetic.main.bottom_sheet_fragment_select_restaurant.*

class SelectRestaurantBottomFragment : BottomSheetDialogFragment() {

    companion object {
        fun show(
            activity: FragmentActivity,
            restaurants: List<RestaurantDto>,
            listener: (RestaurantDto) -> Unit
        ) = SelectRestaurantBottomFragment().apply {
            this.listener = listener
            this.restaurants = restaurants
            show(activity.supportFragmentManager, "SelectRestaurantBottomFragment TAG")
        }
    }

    private lateinit var listener: (RestaurantDto) -> Unit
    private lateinit var restaurants: List<RestaurantDto>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.bottom_sheet_fragment_select_restaurant,
        container,
        false
    )

    private val adapter by lazy {
        getRestaurantSelectingAdapter {
            listener(it)
            dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restaurantsRecycler.adapter = adapter
        adapter.items = listOf(RestaurantsListItem.Header) +
                restaurants.map { RestaurantsListItem.Restaurant(it) }
    }

    override fun onDestroyView() {
        restaurantsRecycler.adapter = null
        super.onDestroyView()
    }

}