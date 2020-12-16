package ru.psu.service.impl

import org.mapstruct.factory.Mappers
import ru.psu.model.ChainSegment
import ru.psu.model.SegmentJoint
import ru.psu.service.mapper.ChainSegmentMapper

class ChainSegmentService private constructor(updateMapper: ChainSegmentMapper): AbstractElementService<ChainSegment, SegmentJoint, ChainSegmentMapper>(
    updateMapper
) {

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

    override fun delete(element: ChainSegment) {
        super.delete(element)
        element.segmentJoint?.removeSegment(element);
    }

    private object HOLDER {
        val INSTANCE = ChainSegmentService(Mappers.getMapper(ChainSegmentMapper::class.java))
    }

    companion object {
        val instance: ChainSegmentService by lazy { HOLDER.INSTANCE }
    }

}