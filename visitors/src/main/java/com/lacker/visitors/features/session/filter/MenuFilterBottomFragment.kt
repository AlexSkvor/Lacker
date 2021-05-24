package com.lacker.visitors.features.session.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lacker.dto.menu.MenuSearchFilter
import com.lacker.visitors.R

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
        // TODO listeners
    }

}

fun Fragment.openMenuFilterDialog(
    filter: MenuSearchFilter?,
    listener: (MenuSearchFilter) -> Unit,
) {
    MenuFilterBottomFragment.show(childFragmentManager, filter, listener)
}