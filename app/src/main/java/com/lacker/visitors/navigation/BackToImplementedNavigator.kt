package com.lacker.visitors.navigation

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.android.support.SupportAppScreen
import ru.terrakok.cicerone.commands.Forward

open class BackToImplementedNavigator(
    activity: FragmentActivity,
    fragmentManager: FragmentManager,
    containerId: Int
): SupportAppNavigator(activity, fragmentManager, containerId) {
    override fun backToUnexisting(screen: SupportAppScreen) {
        super.backToUnexisting(screen)
        fragmentForward(Forward(screen))
    }
}