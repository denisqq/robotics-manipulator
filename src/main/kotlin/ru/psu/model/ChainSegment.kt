package ru.psu.model

import ru.psu.model.enums.ChainElementType
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class ChainSegment(
    override var id: Long?,
    override var weight: Double,
    override var systemCoordinate: SystemCoordinate?,

    var endPoint: Point,
    var startPoint: Point,
    var hidden: Boolean = false,
    var ephemeral: Boolean = false,
    var parentSegmentJoint: SegmentJoint? = null,
    var childSegmentJoint: SegmentJoint? = null
) : AbstractChainElement() {

    fun segmentLength(): Double {
        val xSqr = (this.endPoint.x - this.startPoint.x).pow(2)
        val ySqr = (this.endPoint.y - this.startPoint.y).pow(2)

        return sqrt(xSqr + ySqr)
    }

    override val elementType: ChainElementType
        get() = ChainElementType.SEGMENT

//
//    override fun elementType(): ChainElementType {
//        return chainElementType;
//    }

}