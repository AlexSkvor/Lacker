package com.lacker.visitors.navigation

import androidx.fragment.app.Fragment
import com.lacker.visitors.features.profile.ProfileFragment
import com.lacker.visitors.features.session.menu.MenuFragment
import com.lacker.visitors.features.scan.ScanFragment
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

    object ProfileScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = ProfileFragment.newInstance()
    }

    object OrderHistoryScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO()
    }

    object NewsScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO()
    }

    object SettingsScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO()
    }

    object AboutScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO()
    }

}