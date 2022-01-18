package com.map

import kotlinx.coroutines.*


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
        val fifo: CollectionAddRemove<ElementWait<T>> = createStack(waitBufferCapacity),
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
            is Intent.NewElement -> {
                val (fifo, removed) = state.fifo.add(intent.wait)
                removed?.let {
                    println("drop")//TODO
                    scope.launch {
                        it.deferred.completeExceptionally(Exception("cancelled in decorateWithLimitRequestsInParallel"))
                    }
                }
                state.copy(fifo = fifo).addSideEffect(SideEffect.Delay())
            }
            is Intent.AfterDelay -> {
                if (state.fifo.isNotEmpty()) {
                    var fifo = state.fifo
                    val elementsToLoad: MutableList<ElementWait<T>> = mutableListOf()
                    while (state.currentRequests + elementsToLoad.size < maxParallelRequests && fifo.isNotEmpty()) {
                        val result = fifo.remove()
                        result.removed?.let {
                            elementsToLoad.add(it)
                        }
                        fifo = result.collection
                    }
                    state.copy(
                        fifo = fifo,
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
            println("fifo.size: ${it.fifo.size}")
        }
    }

    return object : TileContentRepository<T> {
        override suspend fun getTileContent(tile: Tile): T {
            return CompletableDeferred<T>()
                .also { store.send(Intent.NewElement(ElementWait(tile, it))) }
                .await()
        }
    }
}

private class ElementWait<T>(val tile: Tile, val deferred: CompletableDeferred<T>)
private sealed interface Intent<T> {
    class ElementComplete<T> : Intent<T>
    class NewElement<T>(val wait: ElementWait<T>) : Intent<T>
    class AfterDelay<T> : Intent<T>
}

private sealed interface SideEffect<T> {
    class Load<T>(val waitElements: List<ElementWait<T>>) : SideEffect<T>
    class Delay<T> : SideEffect<T>
}
