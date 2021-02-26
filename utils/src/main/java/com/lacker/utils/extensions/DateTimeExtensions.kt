package com.lacker.utils.extensions

import android.app.DatePickerDialog
import android.view.View
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.LocalDateTime.ofInstant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private const val SERVER_DATE_FORMAT = "yyyy-MM-dd"
val serverDateFormatter: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern(SERVER_DATE_FORMAT) }

private const val SERVER_FORMAT = "$SERVER_DATE_FORMAT HH:mm:ssX"
val serverFormatter: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern(SERVER_FORMAT) }

private const val USER_DATE_FORMAT = "dd/MM/yyyy"
val userDateFormatter: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern(USER_DATE_FORMAT) }

private const val USER_FORMAT_WITHOUT_SECS = "$USER_DATE_FORMAT HH:mm"
val userFormatterWithoutSecs: DateTimeFormatter by lazy {
    DateTimeFormatter.ofPattern(USER_FORMAT_WITHOUT_SECS)
}

private const val USER_DATE_FORMAT_SPACES = "dd MMM yyyy"
val userDateFormatterSpaces: DateTimeFormatter by lazy {
    DateTimeFormatter.ofPattern(USER_DATE_FORMAT_SPACES)
}

private const val USER_FORMAT_SPACES_WITHOUT_SECS = "HH:mm $USER_DATE_FORMAT_SPACES"
val userFormatterSpacesWithoutSecs: DateTimeFormatter by lazy {
    DateTimeFormatter.ofPattern(USER_FORMAT_SPACES_WITHOUT_SECS)
}

private const val USER_TIME_FORMAT = "HH:mm:ss"
val userTimeFormatter: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern(USER_TIME_FORMAT) }

class DateTimeAdapter {

    @ToJson
    fun toJson(value: OffsetDateTime): String = serverFormatter.format(value)

    @FromJson
    fun fromJson(value: String): OffsetDateTime = OffsetDateTime.from(serverFormatter.parse(value))

}

class DateAdapter {

    @ToJson
    fun toJson(value: LocalDate): String = serverDateFormatter.format(value)

    @FromJson
    fun fromJson(value: String): LocalDate = LocalDate.from(serverDateFormatter.parse(value))

}

fun View.selectDateOnClick(
    defaultDate: LocalDate? = null, // LocalDate.now() will be used if this is null
    onSelect: (LocalDate) -> Unit
) = setOnClickListener {
    val calendar = Calendar.getInstance().apply {
        time = Date.from(
            defaultDate.onNull(LocalDate.now())
                .atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant()
        )
    }

    val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        val resultCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        val date = ofInstant(resultCalendar.time.toInstant(), ZoneId.systemDefault())
            .toLocalDate()

        onSelect(date)
    }

    DatePickerDialog(
        context,
        listener,
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()

}