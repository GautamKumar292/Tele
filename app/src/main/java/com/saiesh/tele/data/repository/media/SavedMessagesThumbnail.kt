package com.saiesh.tele.data.repository.media

import com.saiesh.tele.core.tdlib.client.TdLibClient
import org.drinkless.tdlib.TdApi

internal fun SavedMessagesRepository.fetchThumbnailPathInternal(
    fileId: Int,
    onResult: (String?) -> Unit
) {
    client.send(TdApi.GetFile(fileId)) { result ->
        val file = result as? TdApi.File
        val local = file?.local
        val existingPath = local?.path?.takeIf { it.isNotBlank() && local.isDownloadingCompleted }
        if (existingPath != null) {
            onResult(existingPath)
            return@send
        }
        lateinit var updateHandler: (TdApi.Object?) -> Unit
        updateHandler = thumbnailHandler@{ update ->
            val updateFile = update as? TdApi.UpdateFile ?: return@thumbnailHandler
            if (updateFile.file.id != fileId) return@thumbnailHandler
            val updatedLocal = updateFile.file.local
            val path = updatedLocal?.path?.takeIf { it.isNotBlank() }
            val isReady = updatedLocal?.isDownloadingCompleted == true
            if (path != null && isReady) {
                TdLibClient.removeUpdateHandler(updateHandler)
                onResult(path)
            }
        }
        TdLibClient.addUpdateHandler(updateHandler)
        client.send(TdApi.DownloadFile(fileId, 32, 0, 0, true)) { downloadResult ->
            when (downloadResult) {
                is TdApi.File -> {
                    val downloadedLocal = downloadResult.local
                    val downloadedPath = downloadedLocal?.path?.takeIf { it.isNotBlank() }
                    if (downloadedLocal?.isDownloadingCompleted == true && downloadedPath != null) {
                        TdLibClient.removeUpdateHandler(updateHandler)
                        onResult(downloadedPath)
                    }
                }
                is TdApi.Error -> {
                    TdLibClient.removeUpdateHandler(updateHandler)
                    onResult(null)
                }
            }
        }
    }
}
