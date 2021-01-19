package com.lacker.visitors.features.scan

import com.lacker.visitors.data.storage.session.SessionStorage
import javax.inject.Inject
import com.lacker.visitors.features.scan.ScanMachine.State
import com.lacker.visitors.features.scan.ScanMachine.Wish
import com.lacker.visitors.features.scan.ScanMachine.Result
import com.lacker.visitors.navigation.Screens
import ru.terrakok.cicerone.Router
import voodoo.rocks.flux.Machine

class ScanMachine @Inject constructor(
    private val router: Router,
    private val sessionStorage: SessionStorage
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class Code(val code: String) : Wish()

        object EnableLightning : Wish()
        object DisableLightning : Wish()
        object EnableCamera : Wish()
        object DisableCamera : Wish()
    }

    data class State(
        val loadingInProcess: Boolean = false,
        val cameraEnabled: Boolean = false,
        val lightningEnabled: Boolean = false
    )

    sealed class Result {
        object CorrectRestaurantCode : Result()
        data class Error(val text: String) : Result()
    }

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.Code -> oldState.copy(
            loadingInProcess = true,
            cameraEnabled = false,
            lightningEnabled = false
        ).also { pushResult { checkCode(wish.code) } }
        Wish.EnableLightning -> if (oldState.loadingInProcess) oldState
        else oldState.copy(lightningEnabled = true)
        Wish.DisableLightning -> oldState.copy(lightningEnabled = false)
        Wish.EnableCamera -> if (oldState.loadingInProcess) oldState
        else oldState.copy(cameraEnabled = true)
        Wish.DisableCamera -> oldState.copy(cameraEnabled = false)
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        Result.CorrectRestaurantCode -> oldState.also {
            router.navigateTo(Screens.MenuScreen)
        }
        is Result.Error -> oldState.also { sendMessage(res.text) }
    }

    private suspend fun checkCode(code: String): Result {
        TODO("Parse code, check ids are correct using Api!")
    }

    override fun onBackPressed() {
        // TODO Implement "type-twice" probably
    }
}