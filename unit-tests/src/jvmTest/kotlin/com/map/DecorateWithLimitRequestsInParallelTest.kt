package com.map

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DecorateWithLimitRequestsInParallelTest {
    @Test
    fun testMaximumParallelRequests() {
        val bufferCapacity = 10
        val totalElementsCount = 50 + bufferCapacity
        val results: MutableList<Int> = CopyOnWriteArrayList()
        val jobs: MutableList<Job> = CopyOnWriteArrayList()//todo delete
        val job = Job()
        runTest {
            val counter = AtomicInteger(0)
            val stub = object : TileContentRepository<Int> {
                override suspend fun getTileContent(tile: Tile): Int {
                    delay(Random.nextLong(0, 50))
                    return counter.incrementAndGet()
                }
            }
            val repository = stub.decorateWithLimitRequestsInParallel(this, waitBufferCapacity = bufferCapacity)
            repeat(100) {
                delay(1)
                jobs.add(
                    launch(job) {
                        try {
                            val result = repository.getTileContent(Tile(0, 0, 0))
                            results.add(result)
                        } catch (t: Throwable) {
                            //skip dropped elements
                        }
                    }
                )
            }
            jobs.joinAll()
            val guaranteedLoadedElements = (totalElementsCount - bufferCapacity)..totalElementsCount
            assertTrue(results.containsAll(guaranteedLoadedElements.toList()), "guaranteed elements exist's")
        }
    }
}
