package com.lacker.visitors.data.storage.favourite

import com.lacker.utils.resources.ResourceProvider
import com.lacker.visitors.R
import com.lacker.visitors.data.api.ApiCallResult
import com.lacker.visitors.data.storage.files.FilesManager
import javax.inject.Inject

class FileFavouritesManager @Inject constructor(
    private val filesManager: FilesManager,
    private val resourceProvider: ResourceProvider,
) : FavouritesManager {

    override suspend fun getFavourites(restaurantId: String): ApiCallResult<Set<String>> {
        return try {
            val text = filesManager.getFileTextOrNull(
                restaurantId,
                FilesManager.FileType.FavouriteMenu
            )
            val list = text.orEmpty().split('|').filterNot { it.isEmpty() }.toSet()
            ApiCallResult.Result(list)
        } catch (t: Throwable) {
            ApiCallResult.ErrorOccurred(resourceProvider.getString(R.string.unknownErrorNotification))
        }
    }

    override suspend fun addToFavourites(
        restaurantId: String,
        menuItemId: String
    ): ApiCallResult<Set<String>> {
        return try {
            val text = filesManager.getFileTextOrNull(
                restaurantId,
                FilesManager.FileType.FavouriteMenu
            ).orEmpty()
            val newText = "$text|$menuItemId"
            filesManager.saveToFile(restaurantId, FilesManager.FileType.FavouriteMenu, newText)
            val list = newText.split('|').filterNot { it.isEmpty() }.toSet()
            ApiCallResult.Result(list)
        } catch (t: Throwable) {
            ApiCallResult.ErrorOccurred(resourceProvider.getString(R.string.unknownErrorNotification))
        }
    }

    override suspend fun removeFromFavourites(
        restaurantId: String,
        menuItemId: String
    ): ApiCallResult<Set<String>> {
        return try {
            val text = filesManager.getFileTextOrNull(
                restaurantId,
                FilesManager.FileType.FavouriteMenu
            )
            val list = text.orEmpty().split('|').filterNot { it.isEmpty() || it == menuItemId }
            val newText = list.joinToString(separator = "|")
            filesManager.saveToFile(restaurantId, FilesManager.FileType.FavouriteMenu, newText)
            ApiCallResult.Result(list.toSet())
        } catch (t: Throwable) {
            ApiCallResult.ErrorOccurred(resourceProvider.getString(R.string.unknownErrorNotification))
        }
    }
}