package com.lacker.visitors.navigation

import androidx.fragment.app.Fragment
import com.lacker.visitors.features.session.menu.MenuFragment
import com.lacker.visitors.features.scan.ScanFragment
import com.lacker.visitors.features.session.basket.BasketFragment
import com.lacker.visitors.features.session.favourite.FavouriteFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {

    object HomeScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO()
    }

    object ScanScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = ScanFragment.newInstance()
    }

    object MenuScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = MenuFragment.newInstance()
    }

    object BasketScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = BasketFragment.newInstance()
    }

    object OrderScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO()
    }

    object FavouriteScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = FavouriteFragment.newInstance()
    }

}