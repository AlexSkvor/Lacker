package com.lacker.staff.features.menu

import com.lacker.staff.R
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.menu.MenuMachine.Wish
import com.lacker.staff.features.menu.MenuMachine.State
import com.lacker.staff.utils.addOrReplaceExistingAdapters
import com.lacker.utils.extensions.colorCompat
import kotlinx.android.synthetic.main.fragment_menu.*

class MenuFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = MenuFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_menu

    override val machine by lazy { getMachineFromFactory(MenuMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.menuTitle),
            subtitle = null,
            showBackIcon = true,
            menuResId = null
        )
    }

    private val adapter by lazy {
        listOf(dishAdapter(showNumber = false, showEmptyPortions = true))
    }

    override fun onScreenInit() {
        menuPaginationView.apply {
            swipeRefresh?.setColorSchemeColors(colorCompat(R.color.blue))
            onAsk { performWish(Wish.PaginationAsk(it)) }
            addOrReplaceExistingAdapters(adapter)
            startRefresh()
        }
    }

    override fun render(state: State) {
        menuPaginationView.setList(state.menu)
    }
}