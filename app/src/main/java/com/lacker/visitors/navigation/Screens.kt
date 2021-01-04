package com.lacker.visitors.navigation

import androidx.fragment.app.Fragment
import ru.terrakok.cicerone.android.support.SupportAppScreen
import com.lacker.visitors.features.auth.AuthFragment
import com.lacker.visitors.features.auth.main.MainAuthFragment

object Screens {

    data class AuthFlow(val hasScreensBefore: Boolean) : SupportAppScreen() {
        override fun getFragment(): Fragment = MainAuthFragment.newInstance(hasScreensBefore)
    }

    data class LackerSignIn(val hasScreensBefore: Boolean) : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO()
    }

    data class LackerSignUp(val hasScreensBefore: Boolean) : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO()
    }

    object StaffSignIn : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO()
    }

    object AuthScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = AuthFragment.newInstance()
    }

    object HomeScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = TODO()
    }

}