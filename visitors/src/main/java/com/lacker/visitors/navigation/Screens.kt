package com.lacker.visitors.navigation

import androidx.fragment.app.Fragment
import com.lacker.visitors.features.about.AboutFragment
import com.lacker.visitors.features.history.HistoryFragment
import com.lacker.visitors.features.profile.ProfileFragment
import com.lacker.visitors.features.session.menu.MenuFragment
import com.lacker.visitors.features.scan.ScanFragment
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.lacker.visitors.features.session.dishdetails.DishDetailsFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    object ScanScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = ScanFragment.newInstance()
    }

    object MenuScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = MenuFragment.newInstance()
    }

    object ProfileScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = ProfileFragment.newInstance()
    }

    object OrderHistoryScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = HistoryFragment.newInstance()
    }

    object AboutScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = AboutFragment.newInstance()
    }

    data class DishDetailsScreen(
        val dish: DomainMenuItem,
        val orderId: String?
    ) : SupportAppScreen() {
        override fun getFragment(): Fragment = DishDetailsFragment.newInstance(dish, orderId)
    }
}