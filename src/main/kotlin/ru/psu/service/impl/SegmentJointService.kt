package ru.psu.service.impl

import org.mapstruct.factory.Mappers
import ru.psu.model.ChainSegment
import ru.psu.model.SegmentJoint
import ru.psu.service.mapper.SegmentJointMapper

class SegmentJointService private constructor(updateMapper: SegmentJointMapper): AbstractElementService<SegmentJoint, ChainSegment, SegmentJointMapper>(
    updateMapper
) {

    override fun createElement(element: SegmentJoint, rootElement: ChainSegment?): SegmentJoint {
        val joint = element.copy(
            id = this.generateId(),
            parentSegment = rootElement
        )
        addIndex(joint)
        rootElement?.let {
            it.segmentJoint = joint
        }
        return joint
    }

    override fun delete(element: SegmentJoint) {
        super.delete(element)
        element.parentSegment?.segmentJoint = null
    }

    private object HOLDER {
        val INSTANCE = SegmentJointService(Mappers.getMapper(SegmentJointMapper::class.java))
    }

    companion object {
        val instance: SegmentJointService by lazy { HOLDER.INSTANCE }
    }
}