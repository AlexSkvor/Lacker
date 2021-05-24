package com.lacker.visitors.features.session.common

import com.lacker.visitors.data.dto.menu.DishTag
import java.io.Serializable

data class DomainMenuItem(
    val id: String,
    val name: String,
    val photoFullUrl: String,
    val shortDescription: String,
    val portions: List<DomainPortion>,
    val inFavourites: Boolean,
    val tags: Set<DishTag>,
    val stopped: Boolean,
) : MenuAdapterItem, Serializable
