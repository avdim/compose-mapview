package com.map

import kotlinx.coroutines.*

//todo Unit tests
/**
 * todo doc
 */
fun <T> TileContentRepository<T>.decorateWithLimitRequestsInParallel(
    scope: CoroutineScope,
    maxParallelRequests: Int = 10,
    waitBufferCapacity: Int = 50,
    delayBeforeRequestMs: Long = 50 // Если карта быстро изменяется, то загружать сразу нет смысла
): TileContentRepository<T> {
    val origin = this

    /**
     * Чтобы не имееть дело с mutable состоянием, воспользуемся immutable состоянием внутри Store
     */
    data class State(
        val fifo: List<ElementWait<T>> = emptyList(),
        val currentRequests: Int = 0
    )

    val store = scope.createStoreWithSideEffect<State, Intent<T>, SideEffect<T>>(
        init = State(),
        effectHandler = { store, effect: SideEffect<T> ->
            when (effect) {
                is SideEffect.Load<T> -> {
                    effect.waitElements.forEach { element ->
                        scope.launch {
                            try {
                                val result = origin.getTileContent(element.tile)
                                element.deferred.complete(result)
                            } catch (t: Throwable) {
                                val message = "caught exception in decorateWithLimitRequestsInParallel"
                                element.deferred.completeExceptionally(Exception(message, t))
                            } finally {
                                store.send(Intent.ElementComplete())
                            }
                        }
                    }
                }
                is SideEffect.Delay<T> -> {
                    scope.launch {
                        delay(delayBeforeRequestMs)
                        store.send(Intent.AfterDelay())
                    }
                }
            }
        }
    ) { state, intent: Intent<T> ->
        // Модификация состояния происходит только в этой функции и исполняется в одном потоке
        when (intent) {
            is Intent.New -> {
                val bigList = state.fifo + intent.wait
                for (i in waitBufferCapacity until bigList.size) {
                    val element = bigList[i]
                    scope.launch {
                        println("drop")
                        element.deferred.completeExceptionally(Exception("cancelled in decorateWithLimitRequestsInParallel"))
                    }
                }
                state.copy(
                    fifo = bigList.take(waitBufferCapacity)
                ).addSideEffect(SideEffect.Delay())
            }
            is Intent.AfterDelay -> {
                if (state.fifo.isNotEmpty()) {
                    var fifo = state.fifo
                    val elementsToLoad: MutableList<ElementWait<T>> = mutableListOf()
                    while (state.currentRequests + elementsToLoad.size < maxParallelRequests && fifo.isNotEmpty()) {
                        elementsToLoad.add(fifo.last())
                        fifo = fifo.dropLast(1)
                    }
                    state.copy(
                        fifo = state.fifo.dropLast(elementsToLoad.size),
                        currentRequests = state.currentRequests + elementsToLoad.size
                    ).addSideEffect(SideEffect.Load(elementsToLoad))
                } else {
                    state.noSideEffects()
                }
            }
            is Intent.ElementComplete -> {
                state.copy(
                    currentRequests = state.currentRequests - 1
                ).run {
                    if (state.fifo.isNotEmpty()) {
                        addSideEffect(SideEffect.Delay())
                    } else {
                        noSideEffects()
                    }
                }
            }
        }
    }

    scope.launch {
        store.stateFlow.collect {
            println("INFO decorateWithLimitRequestsInParallel:")
            println("currentRequests: ${it.currentRequests}")
            println("fifoList.size: ${it.fifo.size}")
        }
    }

    return object : TileContentRepository<T> {
        override suspend fun getTileContent(tile: Tile): T {
            return CompletableDeferred<T>()
                .also { store.send(Intent.New(ElementWait(tile, it))) }
                .await()
        }
    }
}

private class ElementWait<T>(val tile: Tile, val deferred: CompletableDeferred<T>)
private sealed interface Intent<T> {
    class ElementComplete<T> : Intent<T>
    class New<T>(val wait: ElementWait<T>) : Intent<T>
    class AfterDelay<T> : Intent<T>
}

private sealed interface SideEffect<T> {
    class Load<T>(val waitElements: List<ElementWait<T>>) : SideEffect<T>
    class Delay<T> : SideEffect<T>
}
