package com.lacker.visitors.navigation

import androidx.fragment.app.Fragment
import ru.terrakok.cicerone.android.support.SupportAppScreen
import com.lacker.visitors.features.auth.AuthFragment

object Screens {

    object AuthScreen : SupportAppScreen() {
        override fun getFragment(): Fragment? = AuthFragment.newInstance()
    }

    object HomeScreen : SupportAppScreen() {
        override fun getFragment(): Fragment? = TODO()
    }

}