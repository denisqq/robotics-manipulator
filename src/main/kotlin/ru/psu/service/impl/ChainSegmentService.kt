package ru.psu.service.impl

import org.mapstruct.factory.Mappers
import ru.psu.model.*
import ru.psu.service.mapper.ChainSegmentMapper
import java.lang.IllegalArgumentException

class ChainSegmentService private constructor(updateMapper: ChainSegmentMapper) :
    AbstractElementService<ChainSegment, SegmentJoint, ChainSegmentMapper>(
        updateMapper
    ) {

    override fun createElement(element: ChainSegment, parentElement: SegmentJoint?): ChainSegment {
        val segment = element.copy(
            id = this.generateId(),
            parentSegmentJoint = parentElement,
            systemCoordinate = createSystemCoordinate(element)
        )
        addIndex(segment)
        parentElement?.let {
            parentElement.addSegment(segment)
        }
        return segment
    }

    override fun update(id: Long, element: ChainSegment): ChainSegment {
        val systemCoordinate = createSystemCoordinate(element)
        element.parentSegmentJoint?.let {
            if(it.maxAngle > systemCoordinate.angle) {
                throw IllegalArgumentException("Cannot rotate element on angle = ${systemCoordinate.angle} because segment joint max angle = ${it.maxAngle}")
            }
        }

        val segment = super.update(id, element)
        segment.childSegmentJoint?.point = segment.endPoint

        return segment;
    }

    override fun delete(element: ChainSegment) {
        super.delete(element)
        element.parentSegmentJoint?.removeSegment(element);

        element.childSegmentJoint?.let {
            SegmentJointService.instance.delete(it)
        }
    }

    private object HOLDER {
        val INSTANCE = ChainSegmentService(Mappers.getMapper(ChainSegmentMapper::class.java))
    }

    companion object {
        val instance: ChainSegmentService by lazy { HOLDER.INSTANCE }
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
            val childElementCenterMassPoint = childElementCenterMass.point;

            val childXPoint = (childElementCenterMassPoint.x + xPoint) / 2
            val childYPoint = (childElementCenterMassPoint.y + yPoint) / 2

            val elementTreeCenterMassPoint = Point(childXPoint, childYPoint)
            val weight = childElementCenterMass.weight + element.weight

            val weightPoint = Point(
                x = childXPoint * weight,
                y = childYPoint * weight
            )

            return CenterMass(elementTreeCenterMassPoint, weight, weightPoint);
        } ?: run {
            val weightPoint = Point(
                x = xPoint * element.weight,
                y = yPoint * element.weight
            )

            val elementCenterMass = Point(xPoint, yPoint)

            return CenterMass(elementCenterMass, element.weight, weightPoint)
        }
    }

}