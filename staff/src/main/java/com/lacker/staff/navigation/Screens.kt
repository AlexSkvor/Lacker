package com.lacker.staff.navigation

import androidx.fragment.app.Fragment
import com.lacker.staff.data.dto.orders.SubOrderListItem
import com.lacker.staff.features.auth.AuthFragment
import com.lacker.staff.features.orders.TasksFragment
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

    data class SuborderScreen(val subOrder: SubOrderListItem) : SupportAppScreen() {
        override fun getFragment(): Fragment = SuborderFragment.newInstance(subOrder)
    }

}