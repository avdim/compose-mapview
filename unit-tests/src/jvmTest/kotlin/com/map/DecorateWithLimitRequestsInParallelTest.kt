package com.map

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

class DecorateWithLimitRequestsInParallelTest {

    /**
     * Провереям что декаратор decorateWithLimitRequestsInParallel
     * гарантированно вернёт нам последние запрошенные элементы
     * количество гарантированных элементов равно размеру bufferCapacity
     */
    @Test
    fun testMaximumParallelRequests() = runBlocking {
        val bufferCapacity = 10
        val totalElementsCount = 50 + bufferCapacity
        val allElements = List(totalElementsCount) { it }
        val jobs: MutableList<Job> = CopyOnWriteArrayList()
        val storeJob = Job()

        //collect elements in MutableList
        val startLoadKeys: MutableList<Int> = CopyOnWriteArrayList()

        val stub = object : ContentRepository<Int, Unit> {
            override suspend fun loadContent(key: Int): Unit {
                startLoadKeys.add(key)
                delay(Random.nextLong(10, 50))
                return Unit
            }
        }

        val repository = stub.decorateWithLimitRequestsInParallel(
            CoroutineScope(storeJob),
            waitBufferCapacity = bufferCapacity
        )

        allElements.forEach {
            delay(1)
            jobs.add(
                launch(/*job*/) {
                    try {
                        val result = repository.loadContent(it)
                    } catch (t: Throwable) {
                        //skip dropped elements
                    }
                }
            )
        }

        jobs.joinAll()

        val guaranteedLoadedElements = allElements.takeLast(bufferCapacity)
        println("expected: $guaranteedLoadedElements")
        println("actual: $startLoadKeys")
        assertTrue(startLoadKeys.containsAll(guaranteedLoadedElements), "guaranteed elements exist's")
        storeJob.complete()
        Unit
    }

}
