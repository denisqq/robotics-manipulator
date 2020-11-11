package ru.psu.service.impl

import ru.psu.model.ChainSegment
import ru.psu.model.Point
import ru.psu.service.ChainSegmentService
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

class ChainSegmentServiceImpl : ChainSegmentService {

    private val segmentIndexes: ConcurrentMap<Int, ChainSegment> = ConcurrentHashMap()
    private val latestIndexId: AtomicInteger = AtomicInteger(0);

    override fun addToChainSegmentIndexes(segment: ChainSegment) {
        val id = latestIndexId.incrementAndGet()
        segment.id = id;
        segmentIndexes[id] = segment
    }

    override fun getLatestSegment(): ChainSegment? {
        return this.segmentIndexes[latestIndexId.get()]
    }

    override fun calculateChainSegmentCenterMas(segment: ChainSegment): Point {
//        TODO("Not yet implemented")
        val chainSegment = segmentIndexes[segment.id]
        return Point(0.0, 0.0);
    }
}