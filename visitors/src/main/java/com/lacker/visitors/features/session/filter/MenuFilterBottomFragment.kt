package com.lacker.visitors.features.session.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lacker.dto.menu.DishTag
import com.lacker.dto.menu.DishTag.*
import com.lacker.dto.menu.MenuSearchFilter
import com.lacker.visitors.R
import kotlinx.android.synthetic.main.bottom_sheet_fragment_menu_filter.*

class MenuFilterBottomFragment : BottomSheetDialogFragment() {

    companion object {
        fun show(
            manager: FragmentManager,
            filter: MenuSearchFilter?,
            listener: (MenuSearchFilter) -> Unit,
        ) = MenuFilterBottomFragment()
            .apply {
                this.filter = filter ?: MenuSearchFilter(emptySet(), "")
                this.listener = listener
                show(manager, "MenuFilterBottomFragment TAG")
            }
    }

    private lateinit var filter: MenuSearchFilter
    private lateinit var listener: (MenuSearchFilter) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_fragment_menu_filter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuFilterSearchField.setText(filter.text)

        chipAlcohol.isChecked = ALCOHOL in filter.tags
        chipDrinks.isChecked = DRINKS in filter.tags
        chipBird.isChecked = BIRD in filter.tags
        chipSea.isChecked = SEA in filter.tags
        chipMeat.isChecked = MEAT in filter.tags
        chipGarnish.isChecked = GARNISH in filter.tags
        chipAsian.isChecked = ASIAN_DISH in filter.tags
        chipDessert.isChecked = DESSERT in filter.tags
        chipSalad.isChecked = SALAD in filter.tags
        chipSandwich.isChecked = SANDWICH in filter.tags
        chipSoup.isChecked = SOUP in filter.tags
        chipOther.isChecked = OTHER in filter.tags

        searchButton.setOnClickListener {
            listener(getFiltersState())
            dismiss()
        }
        clearButton.setOnClickListener {
            listener(MenuSearchFilter(emptySet(), ""))
            dismiss()
        }
    }

    private fun getFiltersState(): MenuSearchFilter {
        val set = mutableSetOf<DishTag>()

        if (chipAlcohol.isChecked) set += ALCOHOL
        if (chipDrinks.isChecked) set += DRINKS
        if (chipBird.isChecked) set += BIRD
        if (chipSea.isChecked) set += SEA
        if (chipMeat.isChecked) set += MEAT
        if (chipGarnish.isChecked) set += GARNISH
        if (chipAsian.isChecked) set += ASIAN_DISH
        if (chipDessert.isChecked) set += DESSERT
        if (chipSalad.isChecked) set += SALAD
        if (chipSandwich.isChecked) set += SANDWICH
        if (chipSoup.isChecked) set += SOUP
        if (chipOther.isChecked) set += OTHER

        return MenuSearchFilter(set, menuFilterSearchField.text?.toString().orEmpty())
    }

}

fun Fragment.openMenuFilterDialog(
    filter: MenuSearchFilter?,
    listener: (MenuSearchFilter) -> Unit,
) {
    MenuFilterBottomFragment.show(childFragmentManager, filter, listener)
}