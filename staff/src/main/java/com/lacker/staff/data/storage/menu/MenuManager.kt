package com.lacker.staff.data.storage.menu

import com.lacker.dto.menu.MenuItem
import com.lacker.staff.data.api.ApiCallResult

interface MenuManager {

    suspend fun getMenu(): ApiCallResult<List<MenuItem>>

}