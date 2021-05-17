package com.lacker.visitors.data.storage.menu

import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.api.NetworkManager
import com.lacker.visitors.data.dto.menu.Menu
import com.lacker.visitors.data.dto.menu.MenuItem
import com.lacker.visitors.data.storage.files.FilesManager
import com.squareup.moshi.Moshi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.time.OffsetDateTime
import javax.inject.Inject

class FileMenuManager @Inject constructor(
    private val net: NetworkManager,
    private val filesManager: FilesManager,
    private val json: Moshi
) : MenuManager {

    companion object {
        private const val SECONDS_IN_MINUTE = 60
    }

    private val mutex = Mutex()

    override suspend fun getMenu(restaurantId: String): ApiCallResult<List<MenuItem>> {

        //TODO remove and use from parameters
        val restaurantId = "83cea05f-2c88-4f1a-8ce9-7644d0fbf6f8"

        try {
            val savedMenu = mutex.withLock {
                getStoredMenu(restaurantId)
                    ?: return getMenuFromServerWithCashing(restaurantId)
            }

            val savedMenuTimestamp = savedMenu.timeStamp

            val now = OffsetDateTime.now()
            if (now.toEpochSecond() - savedMenuTimestamp.toEpochSecond() < SECONDS_IN_MINUTE)
                return ApiCallResult.Result(savedMenu.items)

            val serverMenuTimestamp = when (val res = getServerMenuTimestamp(restaurantId)) {
                is ApiCallResult.Result -> res.value
                is ApiCallResult.ErrorOccurred -> return ApiCallResult.ErrorOccurred(res.text)
            }

            if (savedMenuTimestamp != serverMenuTimestamp)
                return getMenuFromServerWithCashing(restaurantId)

            return ApiCallResult.Result(savedMenu.items)
        } catch (t: Throwable) {
            Timber.e(t)
            return ApiCallResult.ErrorOccurred("Unknown error: ${t.message}") // TODO to resources
        }
    }

    private val storedMenus: MutableMap<String, Menu> = mutableMapOf()

    private suspend fun getStoredMenu(restaurantId: String): Menu? {

        val inMemoryStoredMenu = storedMenus[restaurantId]
        if (inMemoryStoredMenu != null) return inMemoryStoredMenu

        val inFileStoredMenu = getMenuStoredInFile(restaurantId)
        if (inFileStoredMenu != null) storedMenus[restaurantId] = inFileStoredMenu

        return inFileStoredMenu
    }

    private suspend fun getMenuStoredInFile(restaurantId: String): Menu? {
        val text = filesManager.getFileTextOrNull(restaurantId, FilesManager.FileType.Menu)
            ?: return null

        return json.adapter(Menu::class.java).fromJson(text)
    }

    private suspend fun getMenuFromServerWithCashing(restaurantId: String): ApiCallResult<List<MenuItem>> {
        val menu = when (val res = net.callResult { getRestaurantMenu(restaurantId) }) {
            is ApiCallResult.Result -> res.value.menu
            is ApiCallResult.ErrorOccurred -> return ApiCallResult.ErrorOccurred(res.text)
        }

        val menuText = json.adapter(Menu::class.java).toJson(menu)
        filesManager.saveToFile(restaurantId, FilesManager.FileType.Menu, menuText)
        storedMenus[restaurantId] = menu

        return ApiCallResult.Result(menu.items)
    }

    private suspend fun getServerMenuTimestamp(restaurantId: String): ApiCallResult<OffsetDateTime> {
        return when (val res = net.callResult { getRestaurantMenuTimestamp(restaurantId) }) {
            is ApiCallResult.Result -> ApiCallResult.Result(res.value.data.dateTime)
            is ApiCallResult.ErrorOccurred -> ApiCallResult.ErrorOccurred(res.text)
        }
    }
}