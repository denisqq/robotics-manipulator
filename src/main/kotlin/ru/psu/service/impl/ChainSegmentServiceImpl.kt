package ru.psu.service.impl

import org.mapstruct.factory.Mappers
import ru.psu.model.*
import ru.psu.service.ChainSegmentService
import ru.psu.service.mapper.ChainSegmentMapper
import ru.psu.service.validator.impl.ChainSegmentValidatorImpl
import java.util.*

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
//        val firstPoint = point[0]
//        val secondPoint = point[1]
//
//        val x = (secondPoint.y - firstPoint.y) / (firstPoint.x - secondPoint.x)
//        val y = firstPoint.x * x + firstPoint.y

//        val point

        //TODO https://rosettacode.org/wiki/Find_the_intersection_of_two_lines#Java
        return Collections.emptyList()
    }

}