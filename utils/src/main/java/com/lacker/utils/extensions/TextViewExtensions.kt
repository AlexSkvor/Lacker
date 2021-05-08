package com.lacker.utils.extensions

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun TextView.setTextIfNotEquals(newText: String) {
    if (text?.toString().orEmpty() != newText) text = newText
}


fun TextView.setTextRecolored(
    @StringRes fullTextRes: Int,
    @StringRes textToRecolorRes: Int,
    @ColorRes colorRes: Int
) {
    setTextRecolored(
        fullText = context.getString(fullTextRes),
        textToRecolor = context.getString(textToRecolorRes),
        color = getColor(colorRes)
    )
}

fun CharSequence.withTextSizeSpan(textToRecolor: String, size: Int): SpannableString {
    val spannable = SpannableString(this)
    val startIndex = this.indexOf(textToRecolor)
    if (startIndex != -1) {
        spannable.setSpan(
            AbsoluteSizeSpan(size),
            startIndex,
            startIndex + textToRecolor.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannable
}


fun CharSequence.withTextColorSpan(textToRecolor: String, @ColorInt color: Int): SpannableString {
    val spannable = SpannableString(this)
    val startIndex = this.indexOf(textToRecolor)
    if (startIndex != -1) {
        spannable.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            startIndex + textToRecolor.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannable
}

fun CharSequence.withStrikethroughSpan(textToStrikethrough: String): SpannableString {
    val spannable = SpannableString(this)
    val startIndex = this.indexOf(textToStrikethrough)
    if (startIndex != -1) {
        spannable.setSpan(
            StrikethroughSpan(),
            startIndex,
            startIndex + textToStrikethrough.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannable
}

fun CharSequence.withBold(textToMakeBold: String): SpannableString {
    val spannable = SpannableString(this)
    val startIndex = this.indexOf(textToMakeBold)
    if (startIndex != -1) {
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            startIndex,
            startIndex + textToMakeBold.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannable
}

fun TextView.setTextRecolored(fullText: String, textToRecolor: String, @ColorInt color: Int) {
    text = fullText.withTextColorSpan(textToRecolor, color)
}

private class LinearGradientForegroundSpan(
    private val lineWidth: Float,
    private val prefixLength: Float,
    @ColorInt private vararg val colors: Int,
) : CharacterStyle(), UpdateAppearance {
    override fun updateDrawState(tp: TextPaint?) {
        tp?.shader = LinearGradient(
            prefixLength,
            0f,
            prefixLength + lineWidth,
            0f,
            colors,
            null,
            Shader.TileMode.MIRROR
        )
    }
}

fun CharSequence.withGradientTextColorSpan(
    forView: TextView,
    textToRecolor: String,
    @ColorInt vararg colors: Int
): SpannableString {
    require(colors.size >= 2)
    val spannable = SpannableString(this)
    val startIndex = this.indexOf(textToRecolor)

    if (startIndex != -1) {
        spannable.setSpan(
            LinearGradientForegroundSpan(
                lineWidth = forView.paint.measureText(textToRecolor),
                prefixLength = forView.paint.measureText("${this.subSequence(0, startIndex)}"),
                colors = colors,
            ),
            startIndex,
            startIndex + textToRecolor.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannable
}

fun TextView.setTextWithClickableSubString(
    @StringRes textRes: Int,
    @StringRes clickableSubTextRes: Int,
    @ColorRes linkColor: Int? = null,
    onClick: () -> Unit,
) = setTextWithClickableSubString(
    textString = resources.getString(textRes),
    clickableSubstring = resources.getString(clickableSubTextRes),
    linkColor = linkColor,
    onClick = onClick
)

fun TextView.setTextWithClickableSubString(
    textString: CharSequence,
    clickableSubstring: String,
    @ColorRes linkColor: Int? = null,
    onClick: () -> Unit,
) {
    setSubStringsClickable(
        textString,
        listOf(clickableSubstring),
        linkColor,
    ) { onClick.invoke() }
}

fun TextView.setSubStringsClickable(
    textString: CharSequence,
    clickableSubstrings: List<String>,
    @ColorRes linkColor: Int? = null,
    onClick: (which: Int) -> Unit,
) {
    text = getSpannableWithClickableSubstrings(textString, clickableSubstrings, onClick)
    linkColor?.let { setLinkTextColor(ContextCompat.getColor(context, it)) }
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}

private fun getSpannableWithClickableSubstrings(
    textString: CharSequence,
    clickableSubstrings: List<String>,
    onClick: (which: Int) -> Unit,
): SpannableString {
    val textValue = SpannableString(textString)
    clickableSubstrings.forEachIndexed { index, substring ->

        if (!textString.contains(substring, true)) return@forEachIndexed

        val textValueStartIndex = textString.lastIndexOf(substring, ignoreCase = true)
        textValue.setSpan(
            ClickableSpanNoUnderline { onClick(index) },
            textValueStartIndex,
            textValueStartIndex + substring.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return textValue
}

private class ClickableSpanNoUnderline(private val onClickAction: () -> Unit) : ClickableSpan() {

    override fun onClick(view: View) {
        onClickAction()
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }
}