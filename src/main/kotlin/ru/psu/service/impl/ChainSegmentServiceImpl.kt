package ru.psu.service.impl

import org.mapstruct.factory.Mappers
import ru.psu.model.*
import ru.psu.service.ChainSegmentService
import ru.psu.service.mapper.ChainSegmentMapper
import ru.psu.service.validator.impl.ChainSegmentValidatorImpl
import kotlin.math.max
import kotlin.math.min

class ChainSegmentServiceImpl private constructor(updateMapper: ChainSegmentMapper) : ChainSegmentService,
    AbstractElementService<ChainSegment, SegmentJoint, ChainSegmentMapper>(updateMapper) {


    override fun createElement(element: ChainSegment, parentElement: SegmentJoint?): ChainSegment {
        val segment = element.copy(
            id = this.generateId(),
            parentSegmentJoint = parentElement,
            systemCoordinate = createSystemCoordinate(element)
        )

        ChainSegmentValidatorImpl.validate(segment)
        addIndex(segment)
        parentElement?.let {
            parentElement.addSegment(segment)
        }
        return segment
    }

    override fun update(id: Long, element: ChainSegment): ChainSegment {
        val segment = updateChainSegment(element, id)
        segment.childSegmentJoint?.let {
            it.point = segment.endPoint
            SegmentJointService.instance.update(it.id!!, it)
        }

        return segment
    }

    override fun updateWithoutUpdateChild(id: Long, element: ChainSegment): ChainSegment {
        return updateChainSegment(element, id)
    }

    private fun updateChainSegment(element: ChainSegment, id: Long): ChainSegment {
        element.systemCoordinate = createSystemCoordinate(element)
        ChainSegmentValidatorImpl.validate(element)

        return super.update(id, element)
    }

    override fun delete(element: ChainSegment) {
        super.delete(element)
        element.parentSegmentJoint?.removeSegment(element)

        element.childSegmentJoint?.let {
            SegmentJointService.instance.delete(it)
        }
    }

    private object HOLDER {
        val INSTANCE = ChainSegmentServiceImpl(Mappers.getMapper(ChainSegmentMapper::class.java))
    }

    companion object {
        val instance: ChainSegmentServiceImpl by lazy { HOLDER.INSTANCE }
    }

    override fun createSystemCoordinate(element: ChainSegment): SystemCoordinate {
        val startPoint = element.startPoint
        val endPoint = element.endPoint

        return calcAngleBetweenPoints(endPoint, startPoint)
    }

    override fun calculateCenterMass(element: ChainSegment): CenterMass {
        val endPoint = element.endPoint
        val startPoint = element.startPoint

        val xPoint = (endPoint.x + startPoint.x) / 2
        val yPoint = (endPoint.y + startPoint.y) / 2


        element.childSegmentJoint?.let {

            val childElementCenterMass = SegmentJointService.instance.calculateCenterMass(it)
            val childElementCenterMassPoint = childElementCenterMass.point

            val childXPoint = (childElementCenterMassPoint.x + xPoint) / 2
            val childYPoint = (childElementCenterMassPoint.y + yPoint) / 2

            val elementTreeCenterMassPoint = Point(childXPoint, childYPoint)
            val weight = childElementCenterMass.weight + element.weight

            val weightPoint = Point(
                x = childXPoint * weight,
                y = childYPoint * weight
            )

            return CenterMass(elementTreeCenterMassPoint, weight, weightPoint)
        } ?: run {
            val weightPoint = Point(
                x = xPoint * element.weight,
                y = yPoint * element.weight
            )

            val elementCenterMass = Point(xPoint, yPoint)

            return CenterMass(elementCenterMass, element.weight, weightPoint)
        }
    }

    override fun findElement(vararg point: Point): Collection<ChainSegment> {
        val firstPoint = point[0] //that X absic
        val secondPoint = point[1] // that Y ordinate

        //https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
        return index.filter {
            val value = it.value
            if(value.ephemeral) {
                true
            } else{
                doIntersect(firstPoint, value.endPoint, secondPoint, value.startPoint)
            }
        }.values

    }

    // The main function that returns true if line segment 'p1q1'
    // and 'p2q2' intersect.
    private fun doIntersect(p1: Point?, q1: Point?, p2: Point?, q2: Point?): Boolean {
        // Find the four orientations needed for general and
        // special cases
        val o1 = orientation(p1!!, q1!!, p2!!)
        val o2 = orientation(p1, q1, q2!!)
        val o3 = orientation(p2, q2, p1)
        val o4 = orientation(p2, q2, q1)

        // General case
        if (o1 != o2 && o3 != o4) return true

        // Special Cases
        // p1, q1 and p2 are colinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true

        // p1, q1 and q2 are colinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true

        // p2, q2 and p1 are colinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true

        // p2, q2 and q1 are colinear and q1 lies on segment p2q2
        return o4 == 0 && onSegment(p2, q1, q2)
    }


    private fun onSegment(p: Point, q: Point, r: Point): Boolean {
        return q.x <= max(p.x, r.x) && q.x >= min(p.x, r.x) && q.y <= max(p.y, r.y) && q.y >= min(p.y, r.y)
    }


    // To find orientation of ordered triplet (p, q, r).
    // The function returns following values
    // 0 --> p, q and r are colinear
    // 1 --> Clockwise
    // 2 --> Counterclockwise
    private fun orientation(p: Point, q: Point, r: Point): Int {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
        // for details of below formula.
        val value = ((q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y)).toInt()
        if (value == 0) return 0 // colinear
        return if (value > 0) 1 else 2 // clock or counterclock wise
    }

}