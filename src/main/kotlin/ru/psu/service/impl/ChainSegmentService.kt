package ru.psu.service.impl

import ru.psu.model.ChainSegment
import ru.psu.model.SegmentJoint

class ChainSegmentService private constructor(): AbstractElementService<ChainSegment, SegmentJoint>() {

    override fun createElement(element: ChainSegment, rootElement: SegmentJoint?): ChainSegment {
        val segment = element.copy(
            id = this.generateId(),
            segmentJoint = rootElement,
        )
        addIndex(segment)
        rootElement?.let {
            rootElement.addSegment(segment)
        }
        return segment
    }
    private object HOLDER {
        val INSTANCE = ChainSegmentService()
    }

    companion object {
        val instance: ChainSegmentService by lazy { HOLDER.INSTANCE }
    }

}