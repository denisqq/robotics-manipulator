package ru.psu.model

import ru.psu.model.enums.ChainElementType
import kotlin.math.abs

data class ChainSegment(
    override var id: Long?,
    override var weight: Double,
    override val systemCoordinate: SystemCoordinate,

    var endPoint: Point,
    var startPoint: Point,
    var hidden: Boolean = false,
    var ephemeral: Boolean = false,
    var segmentJoint: SegmentJoint? = null,
): AbstractChainElement() {

    fun segmentLength(): Double {
        //TODO
        return abs(endPoint.x - startPoint.x)
    }

    override val elementType: ChainElementType
        get() = ChainElementType.SEGMENT

//
//    override fun elementType(): ChainElementType {
//        return chainElementType;
//    }

}