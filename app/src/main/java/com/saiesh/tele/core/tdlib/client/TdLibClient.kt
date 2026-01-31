package com.saiesh.tele.core.tdlib.client

import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import java.util.concurrent.CopyOnWriteArrayList

object TdLibClient {
    private val updateHandlers = CopyOnWriteArrayList<(TdApi.Object?) -> Unit>()
    private val errorHandlers = CopyOnWriteArrayList<(Throwable?) -> Unit>()

    val client: Client by lazy {
        Client.create(
            { update -> updateHandlers.forEach { it(update) } },
            { error -> errorHandlers.forEach { it(error) } },
            null
        )
    }

    fun addUpdateHandler(handler: (TdApi.Object?) -> Unit) {
        updateHandlers.add(handler)
        client
    }

    fun removeUpdateHandler(handler: (TdApi.Object?) -> Unit) {
        updateHandlers.remove(handler)
    }

    fun addErrorHandler(handler: (Throwable?) -> Unit) {
        errorHandlers.add(handler)
        client
    }

    fun removeErrorHandler(handler: (Throwable?) -> Unit) {
        errorHandlers.remove(handler)
    }
}
