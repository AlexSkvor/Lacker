package com.lacker.mvi.mvi

import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.*
import timber.log.Timber
import com.lacker.mvi.livedata.LiveDataWithDefault
import com.lacker.mvi.livedata.MutableLiveDataWithDefault
import com.lacker.utils.extensions.isMainThread
import kotlin.coroutines.CoroutineContext

abstract class Machine<WISH, RESULT, STATE : Any> : ViewModel(), CoroutineScope {

    private val states by lazy { MutableLiveDataWithDefault(initialState) }
    fun states(): LiveDataWithDefault<STATE> = states

    private val messages = LiveEvent<String>()
    fun messages(): LiveData<String> = messages

    protected abstract val initialState: STATE

    @MainThread
    protected abstract fun onWish(wish: WISH, oldState: STATE)

    @MainThread
    protected abstract fun onResult(res: RESULT, oldState: STATE): STATE

    internal fun start() {
        onStart()
    }

    fun performWish(wish: WISH) {
        synchronized(this) {
            if (isMainThread()) onWish(wish, state!!)
            else onMain { onWish(wish, state!!) }
        }
    }

    protected fun sendMessage(text: String) = messages.postValue(text)

    /**
     * Applies the given [result] to current state.
     */
    protected fun pushResult(result: RESULT) {
        synchronized(this) {
            if (isMainThread()) state = onResult(result, state!!)
            else onMain { state = onResult(result, state!!) }
        }
    }

    /**
     * Invokes the given [task] on IO scope
     * Takes return value of [task] and applies it to the current state
     * Attention: use only for real suspend functions to avoid redundant context switching
     */
    protected fun pushResult(task: suspend () -> RESULT) {
        onIO { pushResult(task()) }
    }

    @Volatile
    private var state: STATE? = null
        @Synchronized set(value) {
            if (value != field) {
                field = value
                value?.let { states.postValue(value) }
            }
        }
        @Synchronized get() = field ?: initialState

    private var parentJob = SupervisorJob()

    override val coroutineContext: CoroutineContext =
        Dispatchers.Main + parentJob + CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable)
        }

    private fun onMain(block: suspend CoroutineScope.() -> Unit) =
        launch(Dispatchers.Main) { block() }

    private fun onIO(block: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.IO) { block() }

    abstract fun onBackPressed()

    override fun onCleared() {
        super.onCleared()
        parentJob.cancelChildren()
    }

    protected fun repeatWithDelay(delay: Long, action: () -> Unit) {
        onIO {
            while (isActive) {
                delay(delay)
                onMain { action() }
            }
        }
    }

    @CallSuper // In case we want to add something for all machines
    protected open fun onStart() {
    }
}