package ru.psu.service.impl

import ru.psu.model.ChainElement
import ru.psu.service.ChainElementService
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong

abstract class AbstractElementService<T: ChainElement, R: ChainElement>: ChainElementService<T, R> {
    private val index: ConcurrentMap<Long, T> = ConcurrentHashMap()
    private val latestIndexId: AtomicLong = AtomicLong(0);

    protected fun addIndex(element: T) {
        index[element.id] = element
    }

    protected fun generateId(): Long {
        return latestIndexId.incrementAndGet()
    }
}