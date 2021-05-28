package com.lacker.staff.features.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lacker.dto.menu.DishTag
import com.lacker.dto.menu.MenuSearchFilter
import com.lacker.staff.R
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

        chipAlcohol.isChecked = DishTag.ALCOHOL in filter.tags
        chipDrinks.isChecked = DishTag.DRINKS in filter.tags
        chipBird.isChecked = DishTag.BIRD in filter.tags
        chipSea.isChecked = DishTag.SEA in filter.tags
        chipMeat.isChecked = DishTag.MEAT in filter.tags
        chipGarnish.isChecked = DishTag.GARNISH in filter.tags
        chipAsian.isChecked = DishTag.ASIAN_DISH in filter.tags
        chipDessert.isChecked = DishTag.DESSERT in filter.tags
        chipSalad.isChecked = DishTag.SALAD in filter.tags
        chipSandwich.isChecked = DishTag.SANDWICH in filter.tags
        chipSoup.isChecked = DishTag.SOUP in filter.tags
        chipOther.isChecked = DishTag.OTHER in filter.tags

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

        if (chipAlcohol.isChecked) set += DishTag.ALCOHOL
        if (chipDrinks.isChecked) set += DishTag.DRINKS
        if (chipBird.isChecked) set += DishTag.BIRD
        if (chipSea.isChecked) set += DishTag.SEA
        if (chipMeat.isChecked) set += DishTag.MEAT
        if (chipGarnish.isChecked) set += DishTag.GARNISH
        if (chipAsian.isChecked) set += DishTag.ASIAN_DISH
        if (chipDessert.isChecked) set += DishTag.DESSERT
        if (chipSalad.isChecked) set += DishTag.SALAD
        if (chipSandwich.isChecked) set += DishTag.SANDWICH
        if (chipSoup.isChecked) set += DishTag.SOUP
        if (chipOther.isChecked) set += DishTag.OTHER

        return MenuSearchFilter(set, menuFilterSearchField.text?.toString().orEmpty())
    }

}

fun Fragment.openMenuFilterDialog(
    filter: MenuSearchFilter?,
    listener: (MenuSearchFilter) -> Unit,
) {
    MenuFilterBottomFragment.show(childFragmentManager, filter, listener)
}