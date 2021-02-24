package com.lacker.visitors.features.session.comment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lacker.utils.extensions.gone
import com.lacker.utils.extensions.setTextIfNotEquals
import com.lacker.utils.extensions.visible
import com.lacker.visitors.R
import com.lacker.visitors.features.session.common.DomainMenuItem
import com.lacker.visitors.features.session.common.DomainPortion
import com.lacker.visitors.views.SmallMenuItemView
import kotlinx.android.synthetic.main.bottom_sheet_fragment_comment_before_order.*


/**
 * Это должен быть всегда один и тот же диалог :(
 * Обновлять в нем ресайклер через адаптер, подаваемый снаружи
 * Для заказа одного блюда (не корзины) - отдельный боттом шит!
 * Кнопку и едит текст тоже как элементы ресайклера?
 */
class CommentBeforeOrderBottomSheetDialogFragment : BottomSheetDialogFragment() {

    fun render(items: List<DomainMenuItem>, commentText: String) {
        lastStateItems = items
        lastStateCommentText = commentText
        renderItems(items)
        val sum = items.sumBy { it.portions.sumBy { p -> p.basketNumber } }
        commentTitle?.text = getString(R.string.youHaveChosenDishes, sum)
        commentField?.setTextIfNotEquals(commentText)
        if (items.isEmpty() && isAdded) dismiss()
    }

    private var lastStateItems: List<DomainMenuItem>? = null
    private var lastStateCommentText: String? = null

    private fun renderItems(items: List<DomainMenuItem>) {
        val ctx = context ?: return
        if (view == null) return
        items.forEachIndexed { i, menuItem ->
            if (basketContainer.childCount <= i)
                basketContainer.addView(SmallMenuItemView(ctx))

            val menuItemView = basketContainer.getChildAt(i) as SmallMenuItemView
            menuItemView.apply {
                setupForMenuItem(
                    index = i,
                    item = menuItem,
                    onAddToBasket = { addToBasketListener?.invoke(it) },
                    removeFromBasket = { removeFromBasketListener?.invoke(it) }
                )
                visible()
            }
        }

        for (i in items.size until basketContainer.childCount)
            basketContainer.getChildAt(i).gone()
    }

    var addToBasketListener: ((DomainPortion) -> Unit)? = null
    var removeFromBasketListener: ((DomainPortion) -> Unit)? = null
    var commentListener: ((String) -> Unit)? = null
    var submitListener: (() -> Unit)? = null
    var onDismissListener: (() -> Unit)? = null

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
        R.layout.bottom_sheet_fragment_comment_before_order,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentField.doAfterTextChanged { commentListener?.invoke(it?.toString().orEmpty()) }
        submitButton.setOnClickListener { submitListener?.invoke() }
        val lastComment = lastStateCommentText ?: return
        val lastItems = lastStateItems ?: return
        render(lastItems, lastComment)
    }

    override fun onDestroy() {
        onDismissListener?.invoke()
        super.onDestroy()
    }

    fun show(manager: FragmentManager?) {
        if (manager != null && !isVisible && !isAdded)
            show(manager, CommentBeforeOrderBottomSheetDialogFragment::class.java.simpleName)
    }
}