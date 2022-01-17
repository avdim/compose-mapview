package com.map

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch


private sealed interface Message<T> {
    class StartDownload<T>(val tile: Tile, val deferred: CompletableDeferred<T>) : Message<T>
    class DownloadComplete<T>(val tile: Tile, val result: T) : Message<T>
}

/**
 * TODO doc
 */
fun <T> TileContentRepository<T>.decorateWithDistinctDownloader(
    scope: CoroutineScope
): TileContentRepository<T> {
    val origin = this
    val actor = scope.actor<Message<T>> {
        // Внутри Actor-а state потоко-безопасный
        // Вся модификация происходит только с одного потока (корутины)
        // Можно работать с mutable переменными без синхронизации
        val currentRequests: MutableMap<Tile, MutableList<CompletableDeferred<T>>> = mutableMapOf()
        while (true) {
            val message = receive()
            when (message) {
                is Message.StartDownload<T> -> {
                    val tileHandlers = currentRequests.getOrPut(message.tile) {
                        val newHandlers = mutableListOf<CompletableDeferred<T>>()
                        scope.launch {//todo вынести из actor
                            channel.send(
                                Message.DownloadComplete(message.tile, origin.getTileContent(message.tile))
                            )
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