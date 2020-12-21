package ru.psu.service.impl

import org.mapstruct.factory.Mappers
import ru.psu.model.*
import ru.psu.service.mapper.SegmentJointMapper
import ru.psu.service.validator.impl.SegmentJointValidatorImpl

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
        SegmentJointValidatorImpl.validate(joint)
        addIndex(joint)
        parentElement?.let {
            it.childSegmentJoint = joint
        }
        return joint
    }

    override fun update(id: Long, element: SegmentJoint): SegmentJoint {
        element.systemCoordinate = createSystemCoordinate(element)

        SegmentJointValidatorImpl.validate(element)
        val segmentJoint = super.update(id, element)

        element.parentSegment?.let {
            it.endPoint = element.point
            ChainSegmentServiceImpl.instance.updateWithoutUpdateChild(it.id!!, it)
        }

        segmentJoint.childSegments.forEach { chainSegment ->
            chainSegment.startPoint = segmentJoint.point
            ChainSegmentServiceImpl.instance.update(chainSegment.id!!, chainSegment)
        }

        return segmentJoint
    }

    override fun delete(element: SegmentJoint) {
        super.delete(element)
        element.parentSegment?.childSegmentJoint = null

        val iterator = element.childSegments.iterator()

        iterator.forEach { chainSegment ->
            iterator.remove()
            ChainSegmentServiceImpl.instance.delete(chainSegment)
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
            val childElementCenterMass = ChainSegmentServiceImpl.instance.calculateCenterMass(chainSegment)
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

    override fun createSystemCoordinate(element: SegmentJoint): SystemCoordinate {
        if (element.parentSegment == null) return SystemCoordinate(0.0);

        val startPoint = element.point
        val endPoint = element.parentSegment.endPoint

        return calcAngleBetweenPoints(endPoint, startPoint)
    }

    override fun findElement(vararg point: Point): Collection<SegmentJoint> {
        val firstPoint = point[0]
        return index.filter { it.value.point == firstPoint}.values
    }

    private object HOLDER {
        val INSTANCE = SegmentJointService(Mappers.getMapper(SegmentJointMapper::class.java))
    }

    companion object {
        val instance: SegmentJointService by lazy { HOLDER.INSTANCE }
    }
}