package ru.psu.service.impl

import ru.psu.model.ChainSegment
import ru.psu.model.Point
import ru.psu.service.ChainSegmentService
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

class ChainSegmentServiceImpl : ChainSegmentService {

    private val segmentIndexes: ConcurrentMap<Int, ChainSegment> = ConcurrentHashMap()
    private val latestIndexId: AtomicInteger = AtomicInteger(0);

    override fun getLatestSegment(): ChainSegment? {
        return this.segmentIndexes[latestIndexId.get()]
    }

    override fun addChainSegment(segment: ChainSegment): ChainSegment {
        if(segmentIndexes.size == 0) {
            val segmentCopy = segment.copy(id = latestIndexId.incrementAndGet())
            addToChainSegmentIndexes(segmentCopy)
            return segmentCopy
        }

        //TODO пока добавляем в последний
        val latestSegment = getLatestSegment()
        latestSegment?.let {
            val segmentCopy = segment.copy(id = latestIndexId.incrementAndGet(), startPoint = it.endPoint, parent = it)
            addToChainSegmentIndexes(segmentCopy)
            latestSegment.children.add(segmentCopy)
            return segmentCopy
        }

        throw IllegalArgumentException("cannot find latest segment")
    }

    private fun addToChainSegmentIndexes(segment: ChainSegment) {
        segmentIndexes[segment.id] = segment
    }

    override fun calculateChainSegmentCenterMas(segment: ChainSegment): Point {
        val chainSegment = segmentIndexes[segment.id]

        chainSegment?.let {
            val concatPoint = it.startPoint.concat(point = it.endPoint)
            return Point(x = concatPoint.x / 2, y = concatPoint.y / 2);
        }

        throw IllegalArgumentException(s = "Illegal segment, cannot find in index map")
    }
}