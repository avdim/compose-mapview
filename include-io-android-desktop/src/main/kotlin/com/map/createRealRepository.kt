package com.map

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch

fun createRealRepository(ktorClient: HttpClient) = object : TileContentRepository<ByteArray> {
    override suspend fun getTileContent(tile: Tile): ByteArray {
        println("LOAD START $tile ${System.currentTimeMillis() % 1000000}")
        val result = ktorClient.get<ByteArray>(tile.tileUrl)
        println("LOAD END $tile ${System.currentTimeMillis() % 1000000}")
        return result
    }
}

private sealed interface Message<T> {
    class StartDownload<T>(val tile: Tile, val deferred: CompletableDeferred<T>) : Message<T>
    class DownloadComplete<T>(val tile: Tile, val result: T) : Message<T>
}

/**
 * TODO doc
 */
fun <T> TileContentRepository<T>.distinctDownloadDecorator(
    scope: CoroutineScope
): TileContentRepository<T> {
    val origin = this
    val actor = scope.actor<Message<T>> {
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
                        origin.getTileContent(message.tile)
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
