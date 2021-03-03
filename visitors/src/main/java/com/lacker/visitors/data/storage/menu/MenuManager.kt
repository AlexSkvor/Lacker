package com.lacker.visitors.data.storage.menu

import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.dto.menu.MenuItem

interface MenuManager {

    suspend fun getMenu(restaurantId: String): ApiCallResult<List<MenuItem>>

}