package com.lacker.staff.navigation

import androidx.fragment.app.Fragment
import com.lacker.staff.data.dto.orders.SubOrderListItem
import com.lacker.staff.features.auth.AuthFragment
import com.lacker.staff.features.menu.MenuFragment
import com.lacker.staff.features.order.OrderDetailsFragment
import com.lacker.staff.features.orders.OrdersListFragment
import com.lacker.staff.features.tasks.TasksFragment
import com.lacker.staff.features.profile.ProfileFragment
import com.lacker.staff.features.suborder.SuborderFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    object SignInScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = AuthFragment.newInstance()
    }

    object TasksScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = TasksFragment.newInstance()
    }

    object ProfileScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = ProfileFragment.newInstance()
    }

    object MenuScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = MenuFragment.newInstance()
    }

    data class SuborderScreen(val subOrder: SubOrderListItem) : SupportAppScreen() {
        override fun getFragment(): Fragment = SuborderFragment.newInstance(subOrder)
    }

    object OrdersListScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = OrdersListFragment.newInstance()
    }

    data class OrderScreen(val orderId: String) : SupportAppScreen() {
        override fun getFragment(): Fragment = OrderDetailsFragment.newInstance(orderId)
    }

}