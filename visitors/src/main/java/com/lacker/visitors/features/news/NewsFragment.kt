package com.lacker.visitors.features.news

import com.lacker.visitors.R
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarFragmentSettings
import com.lacker.visitors.features.news.NewsMachine.Wish
import com.lacker.visitors.features.news.NewsMachine.State

class NewsFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance() = NewsFragment()
    }

    override fun layoutRes(): Int = R.layout.fragment_news

    override val machine by lazy { getMachineFromFactory(NewsMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.newsScreenTitle),
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