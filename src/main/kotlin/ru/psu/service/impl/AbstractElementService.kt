package ru.psu.service.impl

import ru.psu.model.ChainElement
import ru.psu.service.ChainElementService
import ru.psu.service.mapper.ElementUpdateMapper
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong

abstract class AbstractElementService<T : ChainElement, R : ChainElement, M : ElementUpdateMapper<T>>(private val updateMapper: ElementUpdateMapper<T>) :
    ChainElementService<T, R> {
    private val index: ConcurrentMap<Long, T> = ConcurrentHashMap()
    private val latestIndexId: AtomicLong = AtomicLong(0)


    protected fun addIndex(element: T) {
        index[element.id] = element
    }

    protected fun generateId(): Long {
        return latestIndexId.incrementAndGet()
    }

    override fun update(id: Long, element: T): T {
        val indexedEntity = index[id] as T

        if (indexedEntity.deleted) throw IllegalArgumentException("Cannot update deleted element")

        updateMapper.update(element, indexedEntity)
        return indexedEntity
    }

    override fun delete(element: T) {
        element.deleted = true
    }
}