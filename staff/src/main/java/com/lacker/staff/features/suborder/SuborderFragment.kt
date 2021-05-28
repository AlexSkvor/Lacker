package com.lacker.staff.features.suborder

import com.lacker.staff.R
import com.lacker.staff.data.dto.orders.SubOrderListItem
import com.lacker.staff.features.menu.dishAdapter
import com.lacker.staff.features.tasks.adapters.suborderAdapter
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.suborder.SuborderMachine.Wish
import com.lacker.staff.features.suborder.SuborderMachine.State
import com.lacker.staff.utils.addOrReplaceExistingAdapters
import com.lacker.utils.extensions.colorCompat
import com.lacker.utils.extensions.getArgument
import com.lacker.utils.extensions.withArguments
import kotlinx.android.synthetic.main.fragment_only_pagination.*
import voodoo.rocks.paginator.reduce.PaginationList

class SuborderFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance(suborder: SubOrderListItem) = SuborderFragment()
            .withArguments(SUBORDER_KEY to suborder)

        private const val SUBORDER_KEY = "SUBORDER_KEY SuborderFragment"
    }

    override fun layoutRes(): Int = R.layout.fragment_only_pagination

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

    private val adapter by lazy {
        listOf(
            suborderAdapter(
                onViewClick = {},
                acceptInsteadView = false,
                showButton = false
            ),
            dishAdapter(
                showNumber = true,
                showEmptyPortions = false
            ),
            getButtonAdapter { performWish(Wish.OpenFullOrder) }
        )
    }

    override fun onScreenInit() {
        paginationFragmentContainer.apply {
            swipeRefresh?.setColorSchemeColors(colorCompat(R.color.blue))
            onAsk { swipeRefresh?.isRefreshing = false }
            addOrReplaceExistingAdapters(adapter)
        }
        performWish(Wish.Suborder(suborder))
    }

    override fun render(state: State) {
        state.suborder?.let { renderSuborder(it) }
    }

    private fun renderSuborder(suborder: SubOrderListItem) {
        val items = listOf(
            suborder,
            Button(getString(R.string.fullOrderScreenTitle))
        ) + suborder.orderList
        val paginationList = PaginationList.FullData<Any>(1, items)
        paginationFragmentContainer.setList(paginationList)
    }
}