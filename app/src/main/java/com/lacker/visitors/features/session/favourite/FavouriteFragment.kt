package com.lacker.visitors.features.session.favourite

import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.session.SessionScreen
import com.lacker.visitors.features.session.favourite.FavouriteMachine.Wish
import com.lacker.visitors.features.session.favourite.FavouriteMachine.State

class FavouriteFragment : ToolbarFluxFragment<Wish, State>(), SessionScreen {

    companion object {
        fun newInstance() = FavouriteFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_favourite

    override val machine by lazy { getMachineFromFactory(FavouriteMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.favouriteScreenTitle),
            subtitle = null,
            showBackIcon = false,
            menuResId = null
        )
    }

    override fun onScreenInit() {
        //TODO("Not yet implemented")
    }

    override fun render(state: State) {
        //TODO("Not yet implemented")
    }
}