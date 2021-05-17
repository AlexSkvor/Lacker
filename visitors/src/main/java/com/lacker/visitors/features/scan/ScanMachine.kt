package com.lacker.visitors.features.scan

import com.lacker.utils.extensions.onNull
import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.storage.session.Session
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
    private val sessionStorage: SessionStorage,
    private val resourceProvider: ResourceProvider,
    private val net: NetworkManager
) : Machine<Wish, Result, State>() {

    sealed class Wish {
        data class Code(val code: String) : Wish()

        data class ToggleHelp(val show: Boolean? = null) : Wish()

        object EnableLightning : Wish()
        object DisableLightning : Wish()
        object EnableCamera : Wish()
        object DisableCamera : Wish()
    }

    data class State(
        val loadingInProcess: Boolean = false,
        val cameraEnabled: Boolean = false,
        val lightningEnabled: Boolean = false,
        val showHelp: Boolean = false
    )

    sealed class Result {
        object CorrectRestaurantCode : Result()
        data class Error(val text: String) : Result()
    }

    override val initialState: State = State()

    override fun onWish(wish: Wish, oldState: State): State = when (wish) {
        is Wish.Code -> oldState.copy(
            showHelp = false,
            loadingInProcess = true,
            cameraEnabled = false
        ).also { pushResult { checkCode(wish.code) } }
        Wish.EnableLightning -> oldState.copy(lightningEnabled = true)
        Wish.DisableLightning -> oldState.copy(lightningEnabled = false)
        Wish.EnableCamera -> oldState.copy(cameraEnabled = !oldState.loadingInProcess)
        Wish.DisableCamera -> oldState.copy(cameraEnabled = false)
        is Wish.ToggleHelp -> oldState.copy(
            showHelp = (wish.show ?: !oldState.showHelp) && !oldState.loadingInProcess
        )
    }

    override fun onResult(res: Result, oldState: State): State = when (res) {
        Result.CorrectRestaurantCode -> oldState.copy(loadingInProcess = false).also {
            router.replaceScreen(Screens.MenuScreen)
        }
        is Result.Error -> oldState.copy(loadingInProcess = false, cameraEnabled = true)
            .also { sendMessage(res.text) }
    }

    private suspend fun checkCode(code: String): Result {

        val restaurantId = code.substringBefore('|')
        val tableId = code.substringAfter('|')

        if (restaurantId.length != 36 || tableId.length != 36)
            return Result.Error(resourceProvider.getString(R.string.qrCodeInvalidFormat))

        return when (
            val res = net.callResult { getTablesOfRestaurant(restaurantId) }
        ) {
            is ApiCallResult.Result -> {
                val hasTable = res.value.data.map { it.id }.contains(tableId)
                if (hasTable) Result.CorrectRestaurantCode.also {
                    sessionStorage.session = Session(restaurantId, tableId)
                } else Result.Error("Unrecognized table!")
            }
            is ApiCallResult.ErrorOccurred -> Result.Error(res.text)
        }
    }

    private var lastClickTime: Long? = null
    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (lastClickTime == null || now - lastClickTime.onNull(0) > 2000) {
            lastClickTime = now
            sendMessage(resourceProvider.getString(R.string.clickAgainToCloseApp))
        } else router.exit()
    }
}