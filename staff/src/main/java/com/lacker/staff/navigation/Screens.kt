package com.lacker.staff.navigation

import androidx.fragment.app.Fragment
import com.lacker.staff.features.orders.OrdersFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    object OrdersScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = OrdersFragment.newInstance()
    }

    object ProfileScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO("After UserStorage")
    }

}