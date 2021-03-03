package com.lacker.visitors.features.session.callstaff

interface CallStaffView {
    fun showProgress()
    fun showError(text: String)
    fun showSuccess()
}