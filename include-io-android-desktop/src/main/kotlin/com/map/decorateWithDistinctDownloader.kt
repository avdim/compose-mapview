package com.map

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch


private sealed interface Message<T> {
    class StartDownload<T>(val tile: Tile, val deferred: CompletableDeferred<T>) : Message<T>
    class DownloadComplete<T>(val tile: Tile, val result: T) : Message<T>
    class DownloadFail<T>(val tile: Tile, val exception: Throwable) : Message<T>
}

/**
 * TODO doc
 */
fun <T> TileContentRepository<T>.decorateWithDistinctDownloader(
    scope: CoroutineScope
): TileContentRepository<T> {
    val origin = this
    val actor = scope.actor<Message<T>> {
        // Вся модификация происходит только с одного потока (корутины)
        // Можно работать с mutable переменными без синхронизации
        val currentRequests: MutableMap<Tile, MutableList<CompletableDeferred<T>>> = mutableMapOf()
        while (true) {
            val message = receive()
            when (message) {
                is Message.StartDownload<T> -> {
                    val tileHandlers = currentRequests.getOrPut(message.tile) {
                        val newHandlers = mutableListOf<CompletableDeferred<T>>()
                        scope.launch {
                            // Этот код запускается вне actor-а и тут нельзя напрямую менять mutable state
                            // Но можно пробрасывать сообщения обратно в actor
                            try {
                                val result = origin.getTileContent(message.tile)
                                channel.send(
                                    Message.DownloadComplete(message.tile, result)
                                )
                            } catch (t: Throwable) {
                                channel.send(Message.DownloadFail(message.tile, t))
                            }
                        }
                        newHandlers
                    }
                    tileHandlers.add(message.deferred)
                }
                is Message.DownloadComplete<T> -> {
                    currentRequests.remove(message.tile)?.forEach {
                        it.complete(message.result)
                    }
                }
                is Message.DownloadFail<T> -> {
                    val exceptionInfo = "decorateWithDistinctDownloader, fail to load tile ${message.tile}"
                    val exception = Exception(exceptionInfo, message.exception)
                    currentRequests.remove(message.tile)?.forEach {
                        it.completeExceptionally(exception)
                    }
                }
            }
        }
    }

    return object : TileContentRepository<T> {
        override suspend fun getTileContent(tile: Tile): T {
            return CompletableDeferred<T>()
                .also { actor.send(Message.StartDownload(tile, it)) }
                .await()
        }
    }
}
