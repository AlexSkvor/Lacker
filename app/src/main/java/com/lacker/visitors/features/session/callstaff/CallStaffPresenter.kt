package com.lacker.visitors.features.session.callstaff

import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.storage.session.SessionStorage
import com.lacker.visitors.data.storage.user.UserStorage
import com.lacker.visitors.utils.mvp.BaseMvpPresenter
import javax.inject.Inject

class CallStaffPresenter @Inject constructor(
    private val net: NetworkManager,
    private val userStorage: UserStorage,
    private val sessionStorage: SessionStorage,
    private val resourceProvider: ResourceProvider
) : BaseMvpPresenter<CallStaffView>() {

    private var taskExecuting: Boolean = false

    fun callFor(type: CallStaffType) {
        val restaurantId = sessionStorage.session?.restaurantId
        if (restaurantId.isNullOrBlank()) {
            return view.showError(resourceProvider.getString(R.string.shouldSelectRestaurantBeforeCallStaff))
        }

        if (taskExecuting) return
        taskExecuting = true

        view.showProgress()

        launchIo {
            val res = net.callResult { callStaff(restaurantId, type.apiName) } // TODO also use auth token later

            launchUi {
                when (res) {
                    is ApiCallResult.Result -> view.showSuccess()
                    is ApiCallResult.ErrorOccurred -> view.showError(res.text)
                }
                taskExecuting = false
            }
        }
    }


    enum class CallStaffType(val apiName: String) {
        PAYMENT_BANK("bank_payment"),
        PAYMENT_CASH("cash_payment"),
        CONSULTATION("consultation")
    }
}