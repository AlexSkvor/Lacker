package com.lacker.staff

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.lacker.staff.data.storage.user.UserStorage
import com.lacker.staff.di.DependencyProvider
import com.lacker.staff.navigation.Screens
import com.lacker.utils.base.ToolbarFluxFragment
import com.lacker.utils.base.ToolbarOwner
import com.lacker.utils.base.VolumeKeysPressListener
import com.lacker.utils.extensions.loadFromNet
import com.lacker.utils.navigation.BackToImplementedNavigator
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppScreen
import ru.terrakok.cicerone.commands.Command
import voodoo.rocks.flux.interfaces.UserNotifier
import voodoo.rocks.flux.interfaces.ViewModelFactoryProvider
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ViewModelFactoryProvider, UserNotifier, ToolbarOwner {

    private val currentFragment: ToolbarFluxFragment<*, *>?
        get() = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? ToolbarFluxFragment<*, *>

    override fun onCreate(savedInstanceState: Bundle?) {
        DependencyProvider.get().component.inject(this)
        setTheme(R.style.AppThemeNoActionBar)
        super.onCreate(savedInstanceState)
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
    lateinit var userStorage: UserStorage

    private val defaultScreen
        get() = if (userStorage.user.isEmpty()) Screens.SignInScreen
        else Screens.TasksScreen

    private val navigator: Navigator =
        object : BackToImplementedNavigator(this, supportFragmentManager, R.id.fragmentContainer) {
            override fun setupFragmentTransaction(
                command: Command,
                currentFragment: Fragment?,
                nextFragment: Fragment?,
                fragmentTransaction: FragmentTransaction
            ) {
                fragmentTransaction.setCustomAnimations(
                    R.animator.show_fragment_anim,
                    R.animator.hide_fragment_anim,
                    R.animator.slow_show_fragment_anim,
                    R.animator.slow_hide_fragment_anim
                )
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
                renderUserInfo()
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

    private fun renderUserInfo() {
        val user = userStorage.user
        if (user.isEmpty()) return

        val name = user.name + " " + user.surname

        val header = leftNavigation.getHeaderView(0)

        val avatarView = header.findViewById<CircleImageView>(R.id.avatarProfileNavHeader)
        avatarView.loadFromNet("https://i.ytimg.com/vi/Yh5whB-37HY/hqdefault_live.jpg", crossFade = false)

        header.findViewById<TextView>(R.id.usernameNavHeader).text = name
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
        if (drawerLayout.isDrawerOpen(leftNavigation))
            drawerLayout.closeDrawer(leftNavigation)
        else currentFragment?.onBackPressed() ?: super.onBackPressed()
    }

    override fun refreshToolbar() {
        currentFragment?.toolbarSettings?.let {
            supportActionBar?.show()
            supportActionBar?.title = it.title
            supportActionBar?.subtitle = it.subtitle

            if (it.showBackIcon) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                drawerToggle.isDrawerIndicatorEnabled = false
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                drawerToggle.isDrawerIndicatorEnabled = true
            }
            drawerToggle.syncState()

            toolbar?.menu?.clear()
            invalidateOptionsMenu()
        } ?: run {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            supportActionBar?.hide()
        }
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
                R.id.navigateOrders -> defaultScreen
                R.id.navigateMenu -> Screens.MenuScreen
                R.id.navigateAllOrders -> Screens.OrdersListScreen
                else -> null
            }
            requireNotNull(nextScreen)
            navigateFromNavDrawerToScreen(nextScreen)
            false
        }
        leftNavigation.getHeaderView(0)
            .setOnClickListener { navigateFromNavDrawerToScreen(Screens.ProfileScreen) }
        renderUserInfo()
    }

    private fun navigateFromNavDrawerToScreen(nextScreen: SupportAppScreen) {
        if (nextScreen == defaultScreen) router.backTo(null)
        else when (supportFragmentManager.backStackEntryCount) {
            0 -> router.navigateTo(nextScreen)
            1 -> router.replaceScreen(nextScreen)
            else -> router.newRootChain(defaultScreen, nextScreen)
        }

        drawerLayout.closeDrawer(leftNavigation)
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