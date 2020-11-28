package ru.psu.model

import kotlin.math.abs

data class ChainSegment(val id: Int?,
                        val weight: Double,
                        val endPoint: Point,
                        val startPoint: Point,
                        val maxAngle: Double,
                        val parent: ChainSegment? = null,
                        val systemCoordinate: SystemCoordinate,
                        val children: MutableList<ChainSegment> = mutableListOf(),
                        val segmentType: ChainSegmentType = ChainSegmentType.REGULAR
) {

    fun segmentLength(): Double {
        return abs(endPoint.x - endPoint.y)
    }

}