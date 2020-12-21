package ru.psu.model

import ru.psu.model.enums.ChainElementType


data class SegmentJoint(
    override var id: Long?,
    override var weight: Double,
    override var systemCoordinate: SystemCoordinate?,

    var point: Point,
    var maxAngle: Double,
    val parentSegment: ChainSegment?,
    val childSegments: MutableList<ChainSegment> = mutableListOf()
) : AbstractChainElement() {

    override val elementType: ChainElementType
        get() = ChainElementType.JOINT

    fun addSegment(segment: ChainSegment) {
        this.childSegments.add(segment)
    }

    fun removeSegment(segment: ChainSegment) {
        this.childSegments.remove(segment)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SegmentJoint

        if (id != other.id) return false
        if (point != other.point) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + point.hashCode()
        return result
    }


}
