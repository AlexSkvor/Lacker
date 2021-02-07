package com.lacker.visitors.utils.mvp

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class BaseMvpPresenter<V : Any> {
    private lateinit var job: Job
    private lateinit var scope: CoroutineScope

    private var realView: V? = null

    protected val view: V
        get() = realView ?: throw IllegalStateException("View is not initialized!")

    fun bindView(view: V) {
        job = Job()
        scope = CoroutineScope(Dispatchers.IO + job)
        realView = view
        onBind()
    }

    fun unbindView() {
        onUnbind()
        job.cancel()
        realView = null
    }

    protected open fun onUnbind() {}
    protected open fun onBind() {}

    protected fun <P> launchIo(doOnAsyncBlock: suspend CoroutineScope.() -> P) {
        doCoroutineWork(doOnAsyncBlock, scope, Dispatchers.IO)
    }

    protected fun <P> launchUi(doOnAsyncBlock: suspend CoroutineScope.() -> P) {
        doCoroutineWork(doOnAsyncBlock, scope, Dispatchers.Main)
    }

    private inline fun <P> doCoroutineWork(
        crossinline doOnAsyncBlock: suspend CoroutineScope.() -> P,
        coroutineScope: CoroutineScope,
        context: CoroutineContext
    ) {
        coroutineScope.launch {
            withContext(context) {
                doOnAsyncBlock.invoke(this)
            }
        }
    }
}