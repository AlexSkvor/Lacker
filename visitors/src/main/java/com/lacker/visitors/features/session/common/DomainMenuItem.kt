package com.lacker.visitors.features.session.common

import java.io.Serializable

data class DomainMenuItem(
    val id: String,
    val name: String,
    val photoFullUrl: String,
    val shortDescription: String,
    val portions: List<DomainPortion>,
    val inFavourites: Boolean
) : MenuAdapterItem, Serializable
