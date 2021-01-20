package com.lacker.visitors.navigation

import androidx.fragment.app.Fragment
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
        override fun getFragment(): Fragment = TODO()
    }

}