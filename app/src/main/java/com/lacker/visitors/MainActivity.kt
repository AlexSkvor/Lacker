package com.lacker.visitors

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.commands.Command
import com.lacker.visitors.data.storage.UserStorage
import com.lacker.visitors.di.DependencyProvider
import com.lacker.visitors.features.base.ToolbarFluxFragment
import com.lacker.visitors.features.base.ToolbarOwner
import com.lacker.visitors.navigation.BackToImplementedNavigator
import com.lacker.visitors.navigation.FastClickSafeRouter
import com.lacker.visitors.navigation.Screens
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

        if (savedInstanceState == null)
            router.backTo(defaultScreen)
    }

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: FastClickSafeRouter

    @Inject
    lateinit var userStorage: UserStorage

    private val defaultScreen
        get() = if (userStorage.user.isEmpty()) Screens.AuthFlow(false)
        else Screens.HomeScreen

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
        } ?: supportActionBar?.hide()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> false.also { onBackPressed() }
        else -> super.onOptionsItemSelected(item)
    }
}