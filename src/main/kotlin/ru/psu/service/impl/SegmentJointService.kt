package ru.psu.service.impl

import org.mapstruct.factory.Mappers
import ru.psu.model.ChainSegment
import ru.psu.model.SegmentJoint
import ru.psu.service.mapper.SegmentJointMapper

class SegmentJointService private constructor(updateMapper: SegmentJointMapper) :
    AbstractElementService<SegmentJoint, ChainSegment, SegmentJointMapper>(
        updateMapper
    ) {

    override fun createElement(element: SegmentJoint, parentElement: ChainSegment?): SegmentJoint {
        val joint = element.copy(
            id = this.generateId(),
            parentSegment = parentElement
        )
        addIndex(joint)
        parentElement?.let {
            it.parentSegmentJoint = joint
        }
        return joint
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

    private object HOLDER {
        val INSTANCE = SegmentJointService(Mappers.getMapper(SegmentJointMapper::class.java))
    }

    companion object {
        val instance: SegmentJointService by lazy { HOLDER.INSTANCE }
    }
}