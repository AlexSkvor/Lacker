package com.lacker.visitors.data.storage.favourite

import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.storage.files.FilesManager
import javax.inject.Inject

class FileFavouritesManager @Inject constructor(
    private val filesManager: FilesManager
) : FavouritesManager {

    override suspend fun getFavourites(restaurantId: String): ApiCallResult<List<String>> {
        return try {
            val text = filesManager.getFileTextOrNull(
                restaurantId,
                FilesManager.FileType.FavouriteMenu
            )
            val list = text.orEmpty().split('|').filterNot { it.isEmpty() }
            ApiCallResult.Result(list)
        } catch (t: Throwable) {
            ApiCallResult.ErrorOccurred("Unknown error: ${t.message}") // TODO to resources
        }
    }

    override suspend fun addToFavourites(
        restaurantId: String,
        menuItemId: String
    ): ApiCallResult<List<String>> {
        return try {
            val text = filesManager.getFileTextOrNull(
                restaurantId,
                FilesManager.FileType.FavouriteMenu
            ).orEmpty()
            val newText = "$text|$menuItemId"
            filesManager.saveToFile(restaurantId, FilesManager.FileType.FavouriteMenu, newText)
            val list = newText.split('|').filterNot { it.isEmpty() }
            ApiCallResult.Result(list)
        } catch (t: Throwable) {
            ApiCallResult.ErrorOccurred("Unknown error: ${t.message}") // TODO to resources
        }
    }

    override suspend fun removeFromFavourites(
        restaurantId: String,
        menuItemId: String
    ): ApiCallResult<List<String>> {
        return try {
            val text = filesManager.getFileTextOrNull(
                restaurantId,
                FilesManager.FileType.FavouriteMenu
            )
            val list = text.orEmpty().split('|').filterNot { it.isEmpty() || it == menuItemId }
            val newText = list.joinToString(separator = "|")
            filesManager.saveToFile(restaurantId, FilesManager.FileType.FavouriteMenu, newText)
            ApiCallResult.Result(list)
        } catch (t: Throwable) {
            ApiCallResult.ErrorOccurred("Unknown error: ${t.message}") // TODO to resources
        }
    }
}