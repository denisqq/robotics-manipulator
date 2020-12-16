package ru.psu.model

import ru.psu.model.enums.ChainElementType


data class SegmentJoint(
    override var id: Long?,
    override var weight: Double,
    override var systemCoordinate: SystemCoordinate,

    val point: Point,
    var maxAngle: Double,
    val parentSegment: ChainSegment?,
    val segments: MutableList<ChainSegment> = mutableListOf()
) : AbstractChainElement() {

    override val elementType: ChainElementType
        get() = ChainElementType.JOINT

    fun addSegment(segment: ChainSegment) {
        this.segments.add(segment)
    }

    fun removeSegment(segment: ChainSegment) {
        this.segments.remove(segment)
    }
}
