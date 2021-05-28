package com.lacker.staff.features.order

import com.lacker.staff.R
import com.lacker.staff.features.menu.dishAdapter
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarFragmentSettings
import com.lacker.staff.features.order.OrderDetailsMachine.Wish
import com.lacker.staff.features.order.OrderDetailsMachine.State
import com.lacker.staff.features.orders.getOrderAdapter
import com.lacker.staff.features.tasks.adapters.suborderAdapter
import com.lacker.staff.utils.addOrReplaceExistingAdapters
import com.lacker.utils.extensions.colorCompat
import com.lacker.utils.extensions.getArgument
import com.lacker.utils.extensions.withArguments
import kotlinx.android.synthetic.main.fragment_only_pagination.*

class OrderDetailsFragment : ToolbarFluxFragment<Wish, State>() {

    companion object {
        fun newInstance(orderId: String) = OrderDetailsFragment()
            .withArguments(ORDER_ID_KEY to orderId)

        private const val ORDER_ID_KEY = "ORDER_ID_KEY OrderDetailsFragment"
    }

    override fun layoutRes(): Int = R.layout.fragment_only_pagination

    override val machine by lazy { getMachineFromFactory(OrderDetailsMachine::class.java) }

    override val toolbarSettings: ToolbarFragmentSettings by lazy {
        ToolbarFragmentSettings(
            title = getString(R.string.fullOrderScreenTitle),
            subtitle = null,
            showBackIcon = true,
            menuResId = null
        )
    }

    private val orderId: String by lazy { getArgument(ORDER_ID_KEY) }

    private val adapter by lazy {
        listOf(
            getOrderAdapter(
                onButtonClick = { performWish(Wish.Close) },
                buttonText = getString(R.string.markAsPaid),
                hideButtonIfTotNew = true,
            ),
            suborderAdapter(
                onViewClick = {},
                acceptInsteadView = false,
                showButton = false
            ),
            dishAdapter(
                showNumber = true,
                showEmptyPortions = false,
            ),
        )
    }

    override fun onScreenInit() {
        performWish(Wish.SetOrderId(orderId))
        paginationFragmentContainer.apply {
            swipeRefresh?.setColorSchemeColors(colorCompat(R.color.blue))
            onAsk { performWish(Wish.PaginationAsk(it)) }
            addOrReplaceExistingAdapters(adapter)
            startRefresh()
        }
    }

    override fun render(state: State) {
        paginationFragmentContainer.setList(state.items)
    }
}