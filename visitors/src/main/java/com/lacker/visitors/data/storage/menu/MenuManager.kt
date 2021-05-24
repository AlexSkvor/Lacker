package com.lacker.visitors.data.storage.menu

import com.lacker.dto.menu.MenuItem
import com.lacker.visitors.data.api.ApiCallResult

interface MenuManager {

    suspend fun getMenu(restaurantId: String): ApiCallResult<List<MenuItem>>

}