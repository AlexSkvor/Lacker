package com.lacker.visitors.data.storage.files

import android.content.Context
import java.io.File

class FilesManager(
    val context: Context
) {

    sealed class FileType(open val ext: String, open val folderName: String) {

        object Menu : FileType("txt", "menus")

        data class Other(
            override val ext: String,
            override val folderName: String
        ) : FileType(ext, folderName)
    }

    suspend fun saveToFile(fileName: String, fileType: FileType, text: String) {
        val directory = getDirectory(fileType)
        val file = File(directory, fileName + "." + fileType.ext)

        file.writeText(text)
    }

    suspend fun getFileTextOrNull(fileName: String, fileType: FileType): String? {
        val directory = getDirectory(fileType)
        val file = File(directory, fileName + "." + fileType.ext)

        return if (file.exists()) file.readText()
        else null
    }

    private fun getDirectory(fileType: FileType): File {
        val rootDirectory = context.filesDir
        require(rootDirectory.isDirectory) { "Cannot create directory inside non-directory file!" }

        val directory = File(rootDirectory, fileType.folderName)

        if (!directory.exists()) {
            require(directory.mkdir()) {
                "Could not create directory $directory" +
                        "\nRequested path: ${directory.path}" +
                        "\nRequested absolute path: ${directory.absolutePath}"
            }
        }

        return directory
    }

}