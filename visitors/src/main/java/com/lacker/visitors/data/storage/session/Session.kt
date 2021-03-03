package com.lacker.visitors.data.storage.session

data class Session(
    val restaurantId: String,
    val tableId: String,
    val startTimeMillis: Long = System.currentTimeMillis()
) {

    fun startedMoreThanMillisBefore(millis: Long) =
        System.currentTimeMillis() - startTimeMillis > millis

}
