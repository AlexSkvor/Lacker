package com.lacker.visitors.features.session.callstaff

import com.lacker.utils.base.BaseMvpPresenter
import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.storage.session.SessionStorage
import com.lacker.visitors.data.storage.user.UserStorage
import javax.inject.Inject

class CallStaffPresenter @Inject constructor(
    private val net: NetworkManager,
    private val userStorage: UserStorage,
    private val sessionStorage: SessionStorage,
    private val resourceProvider: ResourceProvider
) : BaseMvpPresenter<CallStaffView>() {

    private var taskExecuting: Boolean = false

    fun callFor(type: CallStaffType) {
        val session = sessionStorage.session
            ?: return view.showError(resourceProvider.getString(R.string.shouldSelectRestaurantBeforeCallStaff))

        if (taskExecuting) return
        taskExecuting = true

        view.showProgress()

        launchIo {
            val res = net.callResult {
                callStaff(session.restaurantId, type.apiName, session.tableId)
            }

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