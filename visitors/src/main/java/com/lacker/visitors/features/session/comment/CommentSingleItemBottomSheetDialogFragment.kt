package com.lacker.visitors.features.session.comment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lacker.dto.order.OrderInfo
import com.lacker.visitors.R
import com.lacker.visitors.features.session.common.DomainMenuItem
import kotlinx.android.synthetic.main.bottom_sheet_fragment_comment_single_item.*

class CommentSingleItemBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun open(
            manager: FragmentManager,
            menuItem: DomainMenuItem,
            portionId: String,
            onSubmitClick: (String, OrderInfo, Boolean) -> Unit
        ) = CommentSingleItemBottomSheetDialogFragment().apply {
            val portions = menuItem.portions.filter { it.id == portionId }
                .map { it.copy(basketNumber = 1, orderedNumber = 0) }
            this.menuItem = menuItem.copy(portions = portions)
            this.portionId = portionId
            this.onSubmitClick = onSubmitClick
            show(manager, CommentSingleItemBottomSheetDialogFragment::class.java.simpleName)
        }
    }

    private lateinit var menuItem: DomainMenuItem
    private lateinit var portionId: String
    private lateinit var onSubmitClick: (String, OrderInfo, Boolean) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            BottomSheetBehavior.from(
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            ).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.bottom_sheet_fragment_comment_single_item,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemField.setupForMenuItem(0, menuItem, {}, {}, false)
        submitButton.setOnClickListener {
            onSubmitClick(
                commentField.text?.toString().orEmpty(),
                OrderInfo(portionId, 1),
                drinksAsapCheck.isChecked
            )
            dismiss()
        }
    }
}

fun Fragment.orderSingleItem(
    menuItem: DomainMenuItem,
    portionId: String,
    onSubmitClick: (String, OrderInfo, Boolean) -> Unit
) {
    requireActivity()
    CommentSingleItemBottomSheetDialogFragment.open(
        requireActivity().supportFragmentManager, menuItem, portionId, onSubmitClick
    )
}