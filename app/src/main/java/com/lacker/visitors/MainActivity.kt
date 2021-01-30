package com.lacker.visitors

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.lacker.utils.extensions.appearFromBottom
import com.lacker.utils.extensions.hideBelowBottom
import com.lacker.visitors.data.storage.session.SessionStorage
import kotlinx.android.synthetic.main.activity_main.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.commands.Command
import com.lacker.visitors.di.DependencyProvider
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarOwner
import com.lacker.visitors.features.base.VolumeKeysPressListener
import com.lacker.visitors.features.session.SessionHolder
import com.lacker.visitors.features.session.SessionScreen
import com.lacker.visitors.features.session.basket.BasketFragment
import com.lacker.visitors.features.session.favourite.FavouriteFragment
import com.lacker.visitors.features.session.menu.MenuFragment
import com.lacker.visitors.features.session.order.OrderFragment
import com.lacker.visitors.navigation.BackToImplementedNavigator
import com.lacker.visitors.navigation.Screens
import com.lacker.visitors.views.SessionNavigationView
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.interfaces.UserNotifier
import voodoo.rocks.flux.interfaces.ViewModelFactoryProvider
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ViewModelFactoryProvider, UserNotifier, ToolbarOwner,
    SessionHolder {

    private val currentFragment: ToolbarFluxFragment<*, *>?
        get() = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? ToolbarFluxFragment<*, *>

    override fun onCreate(savedInstanceState: Bundle?) {
        DependencyProvider.get().component.inject(this)
        super.onCreate(savedInstanceState) // TODO splash screen theme
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState == null)
            router.backTo(defaultScreen)

        sessionNavigationBar.onStateChange { onNavigationBarStateChange(it) }
    }

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var sessionStorage: SessionStorage

    private val defaultScreen
        get() = if (sessionStorage.hasSession()) Screens.MenuScreen
        else Screens.ScanScreen

    private val navigator: Navigator =
        object : BackToImplementedNavigator(this, supportFragmentManager, R.id.fragmentContainer) {
            override fun setupFragmentTransaction(
                command: Command,
                currentFragment: Fragment?,
                nextFragment: Fragment?,
                fragmentTransaction: FragmentTransaction
            ) {
                if (nextFragment is SessionScreen) {
                    sessionNavigationBar.appearFromBottom(0)
                } else sessionNavigationBar.hideBelowBottom(0)
                fragmentTransaction.setReorderingAllowed(true)
            }
        }

    override fun onSessionScreenStart(nextFragment: SessionScreen) {
        sessionNavigationBar.appearFromBottom(0)
        sessionNavigationBar.state = when (nextFragment) {
            is MenuFragment -> SessionNavigationView.State.MENU
            is FavouriteFragment -> SessionNavigationView.State.FAVOURITE
            is BasketFragment -> SessionNavigationView.State.BASKET
            is OrderFragment -> SessionNavigationView.State.ORDER
            else -> null
        }
    }

    override fun closeNavigationAnimated() {
        sessionNavigationBar.hideBelowBottom(200)
    }

    override fun showNavigationAnimated() {
        sessionNavigationBar.appearFromBottom(200)
    }

    private fun onNavigationBarStateChange(newState: SessionNavigationView.State) {
        val screen = when (newState) {
            SessionNavigationView.State.MENU -> Screens.MenuScreen
            SessionNavigationView.State.FAVOURITE -> Screens.FavouriteScreen
            SessionNavigationView.State.BASKET -> Screens.BasketScreen
            SessionNavigationView.State.ORDER -> Screens.OrderScreen
        }

        if (screen is Screens.MenuScreen) router.backTo(screen)
        else router.navigateTo(screen)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModelFactoryProvider: ViewModelProvider.Factory
        get() = viewModelFactory

    override fun notify(text: String, toast: Boolean) {
        if (toast) Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        else Snackbar.make(mainActivityContainer, text, Snackbar.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: super.onBackPressed()
    }

    override fun refreshToolbar() {
        currentFragment?.toolbarSettings?.let {
            supportActionBar?.show()
            supportActionBar?.title = it.title
            supportActionBar?.subtitle = it.subtitle
            supportActionBar?.setDisplayHomeAsUpEnabled(it.showBackIcon)
            toolbar?.menu?.clear()
            invalidateOptionsMenu()
        } ?: supportActionBar?.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        currentFragment?.toolbarSettings?.menuResId?.let {
            menuInflater.inflate(it, menu)
        } ?: run { toolbar?.menu?.clear() }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> false.also { onBackPressed() }
        else -> {
            val consumedByFragment = currentFragment?.onMenuItemChosen(item.itemId) ?: false
            if (!consumedByFragment) super.onOptionsItemSelected(item) else false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        val volumeListener = currentFragment as? VolumeKeysPressListener

        return volumeListener?.let {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    it.onVolumeDown()
                    true
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    it.onVolumeUp()
                    true
                }
                else -> super.onKeyDown(keyCode, event)
            }
        } ?: super.onKeyDown(keyCode, event)
    }
}