package com.lacker.visitors.features.auth.bottomdialog

import com.lacker.utils.base.BaseMvpPresenter
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.dto.auth.toDomainUser
import com.lacker.visitors.data.storage.user.UserStorage
import com.lacker.visitors.data.dto.auth.GoogleAuthData
import javax.inject.Inject

class AuthPresenter @Inject constructor(
    private val net: NetworkManager,
    private val userStorage: UserStorage
) : BaseMvpPresenter<AuthView>() {

    private var taskExecuting: Boolean = false

    fun authWithGoogle(data: GoogleAuthData) {
        if (taskExecuting) return
        taskExecuting = true

        view.showProgress()
        launchIo {
            val res = net.callResult { signInWithGoogle(data) }

            launchUi {
                when (res) {
                    is ApiCallResult.Result -> {
                        userStorage.user = res.value.user.toDomainUser()
                        view.onSuccessAuth()
                    }
                    is ApiCallResult.ErrorOccurred -> {
                        view.onAuthError(res.text)
                        taskExecuting = false
                    }
                }
            }
        }
    }
}