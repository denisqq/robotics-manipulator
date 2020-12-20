package ru.psu.service.impl

import ru.psu.model.ChainElement
import ru.psu.model.Point
import ru.psu.model.SystemCoordinate
import ru.psu.service.ChainElementService
import ru.psu.service.mapper.ElementUpdateMapper
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

abstract class AbstractElementService<T : ChainElement, R : ChainElement, M : ElementUpdateMapper<T>>(private val updateMapper: ElementUpdateMapper<T>) :
    ChainElementService<T, R> {
    protected val index: ConcurrentMap<Long, T> = ConcurrentHashMap()
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

    abstract fun createSystemCoordinate(element: T): SystemCoordinate

    protected fun calcAngleBetweenPoints(
        endPoint: Point,
        startPoint: Point
    ): SystemCoordinate {
        val multiplyPoints = (endPoint.x * startPoint.x) + (endPoint.y * startPoint.y)
        println("multiplyPoints = $multiplyPoints")

        val sqrt = sqrt(endPoint.x.pow(2) + endPoint.y.pow(2)) *
                sqrt(startPoint.x.pow(2) + startPoint.y.pow(2))

        println("sqrt = $sqrt")
        val divide = multiplyPoints / sqrt

        println("divide = $divide")
        val radianAngle = acos(divide);
        var angle = radianAngle * 180 / Math.PI

        if(angle.isNaN()) {
            angle = 0.0
        }

        return SystemCoordinate(angle)
    }

}