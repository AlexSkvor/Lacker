package com.lacker.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import coil.load
import coil.transform.RoundedCornersTransformation
import okhttp3.HttpUrl
import java.io.File
import kotlin.math.roundToInt


/**
 * In case of image-loading lib changing all changes should be done only here
 */

fun Int.dpToPixels(context: Context): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    return (this.toFloat() * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun ImageView.loadFromNet(url: String, cornersRoundPx: Int = -1) {
    load(url) {
        crossfade(true)
        if (cornersRoundPx > 0)
            transformations(RoundedCornersTransformation(cornersRoundPx.toFloat()))
    }
}

fun ImageView.loadFromFilePath(filePath: String) {
    val file = File(filePath)
    require(file.exists())
    loadFromFile(file)
}

fun ImageView.loadFromFile(file: File) {
    load(file)
}

fun ImageView.loadFromUri(uri: Uri) {
    load(uri)
}

fun ImageView.loadBitmap(bmp: Bitmap) {
    load(bmp)
}

fun ImageView.loadDrawable(drawable: Drawable) {
    load(drawable)
}

fun ImageView.loadDrawableRes(
    @DrawableRes id: Int,
    crossFade: Boolean = false,
    cornersRoundPx: Int = -1
) {
    load(id) {
        crossfade(crossFade)
        if (cornersRoundPx > 0)
            transformations(RoundedCornersTransformation(cornersRoundPx.toFloat()))
    }
}

fun ImageView.loadFromNet(
    url: String,
    @DrawableRes placeHolder: Int? = null,
    @DrawableRes errorHolder: Int? = null,
    crossFade: Boolean = false,
    cornersRoundPx: Int = -1
) {
    load(url) {
        crossfade(crossFade)

        placeHolder?.let { placeholder(it) }
        errorHolder?.let { error(it) }

        if (cornersRoundPx > 0)
            transformations(RoundedCornersTransformation(cornersRoundPx.toFloat()))
    }
}

fun ImageView.loadFromNetFitSize(url: HttpUrl) {
    load(url)
}

fun TextView.setEndDrawable(@DrawableRes drawable: Int?) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        compoundDrawablesRelative[0],
        compoundDrawablesRelative[1],
        drawable?.let { ContextCompat.getDrawable(context, it) },
        compoundDrawablesRelative[3]
    )
}

fun Bitmap.rotated(degree: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}