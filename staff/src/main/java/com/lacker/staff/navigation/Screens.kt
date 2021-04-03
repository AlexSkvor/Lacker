package com.lacker.staff.navigation

import androidx.fragment.app.Fragment
import com.lacker.staff.features.auth.AuthFragment
import com.lacker.staff.features.orders.OrdersFragment
import com.lacker.staff.features.profile.ProfileFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    object SignInScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = AuthFragment.newInstance()
    }

    object OrdersScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = OrdersFragment.newInstance()
    }

    object ProfileScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = ProfileFragment.newInstance()
    }

}