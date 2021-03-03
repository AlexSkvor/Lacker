package com.lacker.visitors.features.auth.bottomdialog

interface AuthView {

    fun onSuccessAuth()
    fun onAuthError(text: String)
    fun showProgress()

}