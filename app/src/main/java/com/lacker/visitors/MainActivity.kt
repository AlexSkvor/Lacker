package com.lacker.visitors

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import com.lacker.visitors.data.storage.UserStorage
import com.lacker.visitors.di.DependencyProvider
import com.lacker.visitors.navigation.Screens
import com.lacker.mvi.base.BaseActivity
import com.lacker.mvi.base.BaseFragment
import com.lacker.mvi.mvi.UserNotifier
import com.lacker.mvi.mvi.ViewModelFactoryProvider
import javax.inject.Inject

class MainActivity : BaseActivity(), ViewModelFactoryProvider, UserNotifier {

    override val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        DependencyProvider.get().component.inject(this)
        super.onCreate(savedInstanceState) // TODO splash screen theme
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

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
        get() = if (userStorage.user.isEmpty()) Screens.AuthScreen
        else Screens.HomeScreen

    private val navigator: Navigator =
        object : SupportAppNavigator(this, supportFragmentManager, R.id.fragmentContainer) {
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
}