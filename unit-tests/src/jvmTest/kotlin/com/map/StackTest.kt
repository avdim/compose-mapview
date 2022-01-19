package com.map

import com.map.collection.createStack
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class StackTest {


    @Test
    fun testAdd() {
        val stack = createStack<Int>(1)
        assertEquals(1, stack.add(1).collection.add(2).removed)
    }

    @Test
    fun testAddRemove() {
        var stack = createStack<Int>(2)
        stack = stack.add(1).collection.add(2).collection.add(3).collection.add(4).collection

        assertEquals(4, stack.remove().removed)
        assertEquals(3, stack.remove().collection.remove().removed)
        assertEquals(3, stack.add(5).removed)
        assertEquals(2, stack.size)
    }

    @Test
    fun testEmptyStack() {
        val stack = createStack<Int>(1)
        stack.add(1)
        assertNull(stack.remove().collection.remove().removed)
    }

}
