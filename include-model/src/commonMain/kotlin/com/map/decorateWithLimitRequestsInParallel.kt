package com.map

import kotlinx.coroutines.*

//todo Unit tests
/**
 * todo doc
 */
fun <T> TileContentRepository<T>.decorateWithLimitRequestsInParallel(
    scope: CoroutineScope,
    maxParallelRequests: Int = 4,
    waitBufferCapacity: Int = 20,
    delayBeforeRequestMs: Long = 50 // Если карта быстро изменяется, то загружать сразу нет смысла
): TileContentRepository<T> {
    val origin = this

    /**
     * Чтобы не имееть дело с mutable состоянием, воспользуемся immutable состоянием внутри Store
     */
    data class State(
        val fifoList: List<ElementWait<T>> = emptyList(),
        val currentRequests: Int = 0
    )

    val store = scope.createStoreWithSideEffect<State, Intent<T>, SideEffect<T>>(
        init = State(),
        effectHandler = { store, effect: SideEffect<T> ->
            when (effect) {
                is SideEffect.Load<T> -> {
                    scope.launch {
                        try {
                            val result = origin.getTileContent(effect.wait.tile)
                            effect.wait.deferred.complete(result)
                        } catch (t: Throwable) {
                            val message = "caught exception in decorateWithLimitRequestsInParallel"
                            effect.wait.deferred.completeExceptionally(Exception(message, t))
                        } finally {
                            store.send(Intent.ElementComplete())
                        }
                    }
                }
            }
        }
    ) { state, intent: Intent<T> ->
        // Модификация состояния происходит только в этой функции и исполняется в одном потоке
        when (intent) {
            is Intent.New -> {
                if (state.currentRequests < maxParallelRequests) {
                    state.copy(
                        currentRequests = state.currentRequests + 1
                    ).addSideEffect(SideEffect.Load(intent.wait))
                } else {
                    val bigList = state.fifoList + intent.wait
                    for(i in waitBufferCapacity until bigList.size) {
                        val element = bigList[i]
                        scope.launch {
                            println("drop")
                            element.deferred.completeExceptionally(Exception("cancelled in decorateWithLimitRequestsInParallel"))
                        }
                    }
                    state.copy(
                        fifoList = bigList.take(waitBufferCapacity)
                    ).noSideEffects()
                }
            }
            is Intent.ElementComplete -> {
                if (state.fifoList.isEmpty()) {
                    state.copy(
                        currentRequests = state.currentRequests - 1
                    ).noSideEffects()
                } else {
                    val last = state.fifoList.last()
                    state.copy(
                        fifoList = state.fifoList.dropLast(1)
                    ).addSideEffect(SideEffect.Load(last))
                }
            }
        }
    }

    scope.launch {
        store.stateFlow.collect {
            println("INFO decorateWithLimitRequestsInParallel:")
            println("currentRequests: ${it.currentRequests}")
            println("fifoList.size: ${it.fifoList.size}")
        }
    }

    return object : TileContentRepository<T> {
        override suspend fun getTileContent(tile: Tile): T {
            return CompletableDeferred<T>()
                .also { store.send(Intent.New(ElementWait(tile, it))) }
                .await()

            delay(10)//todo
        }
    }
}

private class ElementWait<T>(val tile: Tile, val deferred: CompletableDeferred<T>)
private sealed interface Intent<T> {
    class ElementComplete<T> : Intent<T>
    class New<T>(val wait: ElementWait<T>) : Intent<T>
}

private sealed interface SideEffect<T> {
    class Load<T>(val wait: ElementWait<T>) : SideEffect<T>
}
