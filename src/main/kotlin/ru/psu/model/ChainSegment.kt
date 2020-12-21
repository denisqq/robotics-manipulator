package ru.psu.model

import com.fasterxml.jackson.annotation.JsonIgnore
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
    @JsonIgnore
    var parentSegmentJoint: SegmentJoint? = null,
    var childSegmentJoint: SegmentJoint? = null
) : AbstractChainElement() {

    fun segmentLength(): Double {
        val xSqr = (this.endPoint.x - this.startPoint.x).pow(2)
        val ySqr = (this.endPoint.y - this.startPoint.y).pow(2)

        return sqrt(xSqr + ySqr)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChainSegment

        if (id != other.id) return false
        if (endPoint != other.endPoint) return false
        if (startPoint != other.startPoint) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + endPoint.hashCode()
        result = 31 * result + startPoint.hashCode()
        return result
    }

    override val elementType: ChainElementType
        get() = ChainElementType.SEGMENT



//
//    override fun elementType(): ChainElementType {
//        return chainElementType;
//    }

}