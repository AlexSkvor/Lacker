package com.lacker.visitors.features.auth.bottomdialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lacker.utils.extensions.gone
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.di.DependencyProvider
import com.lacker.visitors.data.dto.auth.GoogleAuthData
import kotlinx.android.synthetic.main.bottom_sheet_auth_fragment.*
import kotlinx.android.synthetic.main.bottom_sheet_auth_fragment.view.*
import timber.log.Timber
import javax.inject.Inject

class AuthBottomSheetDialogFragment : BottomSheetDialogFragment(), AuthView {

    companion object {
        fun show(
            manager: FragmentManager,
            reason: String,
            onSuccess: () -> Unit,
            reasonIsFull: Boolean = false
        ) = AuthBottomSheetDialogFragment().apply {
            this.listener = onSuccess
            this.reason = reason
            this.reasonIsFull = reasonIsFull
            show(manager, "AuthBottomSheetDialogFragment TAG")
        }

        private const val REQUEST_CODE_SIGN_IN_GOOGLE = 50
    }

    @Inject
    lateinit var presenter: AuthPresenter

    @Inject
    lateinit var googleApiClient: GoogleSignInClient

    private var listener: (() -> Unit)? = null // TODO add onError someHow!
    private lateinit var reason: String
    private var reasonIsFull: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DependencyProvider.get().component.inject(this)
        presenter.bindView(this)
        val view = inflater.inflate(R.layout.bottom_sheet_auth_fragment, container, false)
        view.explanation.text = if (!reasonIsFull) getString(R.string.authReason, reason)
        else reason
        view.googleSignIn.setOnClickListener { getGoogleAccount() }
        return view
    }

    private fun getGoogleAccount() {
        val signInIntent: Intent = googleApiClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN_GOOGLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = requireNotNull(completedTask.getResult(ApiException::class.java))

            val data = GoogleAuthData(
                googleId = requireNotNull(account.id),
                name = requireNotNull(account.givenName),
                surname = requireNotNull(account.familyName),
                email = requireNotNull(account.email)
            )

            presenter.authWithGoogle(data)
        } catch (e: ApiException) {
            Timber.e(e)
            // TODO show message for user!
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            // TODO show message for user (some empty fields)!
        }
    }

    override fun onDestroyView() {
        presenter.unbindView()
        listener = null
        super.onDestroyView()
    }

    override fun onSuccessAuth() {
        listener?.invoke()
        dismiss()
    }

    override fun onAuthError(text: String) {
        isCancelable = true
        authProgress.gone()
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        isCancelable = false
        authProgress.visible()
    }
}

fun FragmentActivity.withAuthCheck(
    reasonIsFull: Boolean = false,
    @StringRes reasonRes: Int,
    onSuccess: () -> Unit
) {
    val user = DependencyProvider.get().component.getUserStorage().user
    if (!user.isEmpty()) return onSuccess()
    AuthBottomSheetDialogFragment.show(
        supportFragmentManager,
        getString(reasonRes),
        onSuccess,
        reasonIsFull
    )
}

fun Fragment.withAuthCheck(
    reasonIsFull: Boolean = false,
    @StringRes reasonRes: Int,
    onSuccess: () -> Unit
) = requireActivity().withAuthCheck(reasonIsFull, reasonRes, onSuccess)