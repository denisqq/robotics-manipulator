package ru.psu.service

import ru.psu.model.ChainSegment
import ru.psu.model.Point

interface ChainSegmentService {
    fun addToChainSegmentIndexes(segment: ChainSegment)
    fun getLatestSegment(): ChainSegment?
    fun calculateChainSegmentCenterMas(segment: ChainSegment): Point
}