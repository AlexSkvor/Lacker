package com.lacker.visitors.features.session.callstaff

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lacker.utils.extensions.gone
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.di.DependencyProvider
import com.lacker.visitors.features.auth.bottomdialog.withAuthCheck
import kotlinx.android.synthetic.main.bottom_sheet_fragment_call_staff.view.*
import javax.inject.Inject

class CallStaffBottomFragment : BottomSheetDialogFragment(), CallStaffView {

    companion object {
        fun show(manager: FragmentManager) = CallStaffBottomFragment().apply {
            show(manager, "CallStaffBottomFragment TAG")
        }
    }

    @Inject
    lateinit var presenter: CallStaffPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DependencyProvider.get().component.inject(this)
        presenter.bindView(this)
        val view = inflater.inflate(R.layout.bottom_sheet_fragment_call_staff, container, false)
        view.bankCardPaymentButton.setOnClickListener {
            presenter.callFor(CallStaffPresenter.CallStaffType.PAYMENT_BANK)
        }
        view.cashPaymentButton.setOnClickListener {
            presenter.callFor(CallStaffPresenter.CallStaffType.PAYMENT_CASH)
        }
        view.consultationButton.setOnClickListener {
            presenter.callFor(CallStaffPresenter.CallStaffType.CONSULTATION)
        }
        return view
    }

    override fun showProgress() {
        isCancelable = false
        view?.callStaffProgress?.visible()
    }

    override fun showError(text: String) {
        isCancelable = true
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
        view?.callStaffProgress?.gone()
    }

    override fun showSuccess() {
        isCancelable = true
        Toast.makeText(
            requireContext(),
            getString(R.string.weWillNotifyStaff),
            Toast.LENGTH_LONG
        ).show()
        dismiss()
    }

    override fun onDestroyView() {
        presenter.unbindView()
        super.onDestroyView()
    }

}

fun Fragment.openCallStaffDialog() {
    withAuthCheck(false, R.string.staffCalling) {
        CallStaffBottomFragment.show(childFragmentManager)
    }
}