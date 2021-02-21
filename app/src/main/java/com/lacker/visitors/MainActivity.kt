package com.lacker.visitors

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.lacker.visitors.data.storage.session.SessionStorage
import kotlinx.android.synthetic.main.activity_main.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.commands.Command
import com.lacker.visitors.di.DependencyProvider
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarOwner
import com.lacker.visitors.features.base.VolumeKeysPressListener
import com.lacker.visitors.navigation.BackToImplementedNavigator
import com.lacker.visitors.navigation.Screens
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.interfaces.UserNotifier
import voodoo.rocks.flux.interfaces.ViewModelFactoryProvider
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ViewModelFactoryProvider, UserNotifier, ToolbarOwner {

    private val currentFragment: ToolbarFluxFragment<*, *>?
        get() = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? ToolbarFluxFragment<*, *>

    override fun onCreate(savedInstanceState: Bundle?) {
        DependencyProvider.get().component.inject(this)
        super.onCreate(savedInstanceState) // TODO splash screen theme
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        setupNavigationDrawer()

        if (savedInstanceState == null)
            router.replaceScreen(defaultScreen)
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
                fragmentTransaction.setReorderingAllowed(true)
            }
        }

    private val drawerToggle by lazy {
        object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.openDrawer,
            R.string.closeDrawer
        ) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }
        }.apply {
            drawerLayout.addDrawerListener(this)
            syncState()
            setToolbarNavigationClickListener { onBackPressed() }
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setHomeButtonEnabled(true)
        }
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
        else {
            val snack = Snackbar.make(fragmentContainer, text, Snackbar.LENGTH_LONG)
            val params = snack.view.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.TOP
            snack.view.layoutParams = params
            snack.show()
        }
    }

    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: super.onBackPressed()
    }

    override fun refreshToolbar() {
        currentFragment?.toolbarSettings?.let {
            supportActionBar?.show()
            supportActionBar?.title = it.title
            supportActionBar?.subtitle = it.subtitle

            if (it.showBackIcon) {
                drawerToggle.isDrawerIndicatorEnabled = false
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                drawerToggle.isDrawerIndicatorEnabled = true
            }
            drawerToggle.syncState()

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

    private fun setupNavigationDrawer() {
        leftNavigation.setNavigationItemSelectedListener { item ->
            val nextScreen = when (item.itemId) {
                R.id.navigateProfile -> Screens.ProfileScreen
                R.id.navigateMyOrder -> defaultScreen
                R.id.navigateHistory -> Screens.OrderHistoryScreen
                R.id.navigateNews -> Screens.NewsScreen
                R.id.navigateSettings -> Screens.SettingsScreen
                R.id.navigateAbout -> Screens.AboutScreen
                else -> null
            }
            requireNotNull(nextScreen)

            if (nextScreen == defaultScreen) router.backTo(null)
            else when (supportFragmentManager.backStackEntryCount) {
                0 -> router.navigateTo(nextScreen)
                1 -> router.replaceScreen(nextScreen)
                else -> router.newRootChain(defaultScreen, nextScreen)
            }

            drawerLayout.closeDrawer(leftNavigation) // TODO nav drawer text color
            false
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