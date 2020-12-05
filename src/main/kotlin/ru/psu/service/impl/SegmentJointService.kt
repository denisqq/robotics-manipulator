package ru.psu.service.impl

import ru.psu.model.ChainSegment
import ru.psu.model.SegmentJoint

class SegmentJointService private constructor(): AbstractElementService<SegmentJoint, ChainSegment>() {

    override fun createElement(element: SegmentJoint, rootElement: ChainSegment?): SegmentJoint {
        val joint = element.copy(
            id = this.generateId(),
        )
        addIndex(joint)
        rootElement?.let {
            it.segmentJoint = joint
        }
        return joint
    }

    private object HOLDER {
        val INSTANCE = SegmentJointService()
    }

    companion object {
        val instance: SegmentJointService by lazy { HOLDER.INSTANCE }
    }
}