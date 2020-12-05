package ru.psu.model

import ru.psu.model.enums.ChainElementType

data class SegmentJoint(
    override var id: Long?,
    override var weight: Double,
    override val systemCoordinate: SystemCoordinate,

    val point: Point,
    var maxAngle: Double,
    val segments: MutableList<ChainSegment> = mutableListOf()
) : AbstractChainElement() {

    override val elementType: ChainElementType
        get() = ChainElementType.JOINT

    fun addSegment(segment: ChainSegment) {
        this.segments.add(segment)
    }
}
