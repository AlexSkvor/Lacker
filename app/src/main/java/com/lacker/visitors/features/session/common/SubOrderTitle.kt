package com.lacker.visitors.features.session.common

import java.time.OffsetDateTime

data class SubOrderTitle(
    val comment: String,
    val drinksImmediately: Boolean,
    val dateTime: OffsetDateTime?
) : MenuAdapterItem