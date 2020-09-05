package com.alexskvor.mvi.mvi

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.alexskvor.mvi.base.BaseFragment
import com.alexskvor.utils.extensions.getImplementation

abstract class MviFragment <W, S : Any> : BaseFragment() {

    abstract val machine: Machine<W, *, S>
    abstract fun onScreenInit()
    abstract fun render(state: S)

    protected fun getMachineFromFactory(klacc: Class<out Machine<W, *, S>>): Machine<W, *, S> {
        val factory = getImplementation(ViewModelFactoryProvider::class.java)?.viewModelFactoryProvider
            ?: error("ViewModelFactoryProvider is not found")

        return ViewModelProvider(this, factory).get(screenTag, klacc)
    }

    private val screenTag by lazy { hashCode().toString() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        machine.start()
        machine.states().observe { render(it) }
        machine.messages().observe { getImplementation(UserNotifier::class.java)?.notify(it) }
        onScreenInit()
    }

    protected fun <T> LiveData<T>.observe(observer: (T) -> Unit) {
        observe(viewLifecycleOwner, { observer(it) })
    }

    protected fun performWish(wish: W) = machine.performWish(wish)

    override fun onBackPressed() = machine.onBackPressed()

}

interface ViewModelFactoryProvider {
    val viewModelFactoryProvider: ViewModelProvider.Factory
}

interface UserNotifier {
    fun notify(text: String, toast: Boolean = false)
}