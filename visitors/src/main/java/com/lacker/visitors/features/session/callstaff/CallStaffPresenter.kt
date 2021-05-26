package com.lacker.visitors.features.session.callstaff

import com.lacker.dto.appeal.AppealType
import com.lacker.utils.base.BaseMvpPresenter
import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.dto.appeal.CreateAppealRequest
import com.lacker.visitors.data.storage.session.SessionStorage
import javax.inject.Inject

class CallStaffPresenter @Inject constructor(
    private val net: NetworkManager,
    private val sessionStorage: SessionStorage,
    private val resourceProvider: ResourceProvider
) : BaseMvpPresenter<CallStaffView>() {

    private var taskExecuting: Boolean = false

    fun callFor(type: AppealType) {
        val session = sessionStorage.session
            ?: return view.showError(resourceProvider.getString(R.string.shouldSelectRestaurantBeforeCallStaff))

        if (taskExecuting) return
        taskExecuting = true

        view.showProgress()

        val request = CreateAppealRequest(
            tableId = session.tableId,
            target = type,
        )

        launchIo {
            val res = net.callResult { callStaff(request) }

            launchUi {
                when (res) {
                    is ApiCallResult.Result -> view.showSuccess()
                    is ApiCallResult.ErrorOccurred -> view.showError(res.text)
                }
                taskExecuting = false
            }
        }
    }

}