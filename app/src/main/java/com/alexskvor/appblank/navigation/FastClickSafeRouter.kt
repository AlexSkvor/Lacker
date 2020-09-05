package com.alexskvor.appblank.navigation

import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen

private const val MINIMAL_DELAY = 500L

class FastClickSafeRouter : Router() {

    override fun navigateTo(screen: Screen) {
        withDelayCheck { super.navigateTo(screen) }
    }

    override fun newRootScreen(screen: Screen) {
        withDelayCheck { super.newRootScreen(screen) }
    }

    override fun replaceScreen(screen: Screen) {
        withDelayCheck { super.replaceScreen(screen) }
    }

    override fun backTo(screen: Screen?) {
        withDelayCheck { super.backTo(screen) }
    }

    override fun newChain(vararg screens: Screen) {
        withDelayCheck { super.newChain(*screens) }
    }

    override fun newRootChain(vararg screens: Screen) {
        withDelayCheck { super.newRootChain(*screens) }
    }

    override fun finishChain() {
        withDelayCheck { super.finishChain() }
    }

    override fun exit() {
        withDelayCheck { super.exit() }
    }

    private var lastNavigateTo = 0L
    private fun withDelayCheck(action: () -> Unit) {
        if (System.currentTimeMillis() - lastNavigateTo > MINIMAL_DELAY) {
            lastNavigateTo = System.currentTimeMillis()
            action()
        }
    }
}