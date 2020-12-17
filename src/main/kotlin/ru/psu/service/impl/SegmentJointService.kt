package ru.psu.service.impl

import org.mapstruct.factory.Mappers
import ru.psu.model.*
import ru.psu.service.mapper.SegmentJointMapper

class SegmentJointService private constructor(updateMapper: SegmentJointMapper) :
    AbstractElementService<SegmentJoint, ChainSegment, SegmentJointMapper>(
        updateMapper
    ) {

    override fun createElement(element: SegmentJoint, parentElement: ChainSegment?): SegmentJoint {
        val joint = element.copy(
            id = this.generateId(),
            parentSegment = parentElement,
            systemCoordinate = createSystemCoordinate(element)
        )
        addIndex(joint)
        parentElement?.let {
            it.parentSegmentJoint = joint
        }
        return joint
    }

    override fun createSystemCoordinate(element: SegmentJoint): SystemCoordinate {
        if (element.parentSegment == null) return SystemCoordinate(0.0);

        val startPoint = element.point
        val endPoint = element.parentSegment.endPoint

        return calcAngleBetweenPoints(endPoint, startPoint)
    }

    override fun update(id: Long, element: SegmentJoint): SegmentJoint {
        val segmentJoint = super.update(id, element)
        segmentJoint.childSegments.forEach { chainSegment ->
            chainSegment.startPoint = segmentJoint.point
        }

        return segmentJoint
    }

    override fun delete(element: SegmentJoint) {
        super.delete(element)
        element.parentSegment?.parentSegmentJoint = null

        element.childSegments.forEach { chainSegment ->
            ChainSegmentService.instance.delete(chainSegment)
        }
    }

    override fun calculateCenterMass(element: SegmentJoint): CenterMass {
        val elementPoint = element.point
        val elementCenterMassPoint = elementPoint.copy()
        val elementWeightPoint = Point(
            x = elementCenterMassPoint.x * element.weight,
            y = elementCenterMassPoint.y * element.weight
        )

        var segmentJointCenterMass = CenterMass(elementCenterMassPoint, element.weight, elementWeightPoint)

        element.childSegments.forEach{ chainSegment ->
            val childElementCenterMass = ChainSegmentService.instance.calculateCenterMass(chainSegment)
            val childElementCenterMassPoint = childElementCenterMass.point;

            val childXPoint = (childElementCenterMassPoint.x + segmentJointCenterMass.point.x) / 2
            val childYPoint = (childElementCenterMassPoint.y + segmentJointCenterMass.point.y) / 2

            val elementTreeCenterMassPoint = Point(childXPoint, childYPoint)
            val weight = segmentJointCenterMass.weight + childElementCenterMass.weight
            val weightPoint = Point(x = childXPoint * weight, y = childYPoint * weight);

            segmentJointCenterMass = CenterMass(elementTreeCenterMassPoint, weight, weightPoint)
        }

        return segmentJointCenterMass
    }

    private object HOLDER {
        val INSTANCE = SegmentJointService(Mappers.getMapper(SegmentJointMapper::class.java))
    }

    companion object {
        val instance: SegmentJointService by lazy { HOLDER.INSTANCE }
    }
}