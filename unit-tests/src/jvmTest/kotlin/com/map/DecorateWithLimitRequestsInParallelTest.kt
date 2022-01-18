package com.map

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

class DecorateWithLimitRequestsInParallelTest {
    @Test
    fun testMaximumParallelRequests() = runTest {
        val bufferCapacity = 10
        val totalElementsCount = 50 + bufferCapacity
        val startLoadKeys: MutableList<Int> = CopyOnWriteArrayList()
        val jobs: MutableList<Job> = CopyOnWriteArrayList()

        val stub = object : ContentRepository<Int, Unit> {
            override suspend fun loadContent(key: Int): Unit {
                startLoadKeys.add(key)
                delay(Random.nextLong(10, 50))
                return Unit
            }
        }

        val repository = stub.decorateWithLimitRequestsInParallel(CoroutineScope(), waitBufferCapacity = bufferCapacity)

        val counter = AtomicInteger(0)
        repeat(totalElementsCount) {
            delay(1)
            jobs.add(
                launch(/*job*/) {
                    try {
                        val result = repository.loadContent(counter.incrementAndGet())
                    } catch (t: Throwable) {
                        //skip dropped elements
                    }
                }
            )
        }
        jobs.joinAll()
        val guaranteedLoadedElements = (totalElementsCount - bufferCapacity)..totalElementsCount
        println(startLoadKeys)
        assertTrue(startLoadKeys.containsAll(guaranteedLoadedElements.toList()), "guaranteed elements exist's")

        this.cancel()
    }
}
