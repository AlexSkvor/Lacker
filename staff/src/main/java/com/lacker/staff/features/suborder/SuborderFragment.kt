package com.lacker.staff.features.suborder

import com.lacker.staff.R
import com.lacker.staff.data.dto.orders.SubOrderListItem
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.suborder.SuborderMachine.Wish
import com.lacker.staff.features.suborder.SuborderMachine.State
import com.lacker.utils.extensions.getArgument
import com.lacker.utils.extensions.withArguments

class SuborderFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance(suborder: SubOrderListItem) = SuborderFragment()
            .withArguments(SUBORDER_KEY to suborder)

        private const val SUBORDER_KEY = "SUBORDER_KEY SuborderFragment"
    }

    override fun layoutRes(): Int = R.layout.fragment_suborder

    override val machine by lazy { getMachineFromFactory(SuborderMachine::class.java) }

    private val suborder: SubOrderListItem by lazy { getArgument(SUBORDER_KEY) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.suborderDetailsTitle),
            subtitle = null,
            showBackIcon = true,
            menuResId = null
        )
    }

    override fun onScreenInit() {
        performWish(Wish.Suborder(suborder))
        //TODO("Not yet implemented")
    }

    override fun render(state: State) {
        state.suborder?.let { renderSuborder(it) }
    }

    private fun renderSuborder(suborder: SubOrderListItem){
        //TODO("Not yet implemented")
    }
}