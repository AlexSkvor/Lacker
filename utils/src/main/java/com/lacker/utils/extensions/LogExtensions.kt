package com.lacker.utils.extensions

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import okio.Buffer
import timber.log.Timber

@Deprecated(message = "Only debug mode. Delete before commit", level = DeprecationLevel.WARNING)
inline fun <reified T> T.alsoPrintDebug(msg: String) = also { Timber.e("$msg... $it") }

private val json = Moshi.Builder().build()
private const val INDENT = "  "
fun String.formatJson(): String {
    val buffer = Buffer().writeUtf8(this)
    val reader = JsonReader.of(buffer)
    return try {
        json.adapter(Any::class.java).indent(INDENT).toJson(reader.readJsonValue())
    } catch (t: Throwable) {
        this
    }
}