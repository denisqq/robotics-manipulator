package ru.psu.service.impl

import org.mapstruct.factory.Mappers
import ru.psu.model.ChainSegment
import ru.psu.model.SegmentJoint
import ru.psu.service.mapper.ChainSegmentMapper

class ChainSegmentService private constructor(updateMapper: ChainSegmentMapper): AbstractElementService<ChainSegment, SegmentJoint, ChainSegmentMapper>(
    updateMapper
) {

    override fun createElement(element: ChainSegment, parentElement: SegmentJoint?): ChainSegment {
        val segment = element.copy(
            id = this.generateId(),
            parentSegmentJoint = parentElement,
        )
        addIndex(segment)
        parentElement?.let {
            parentElement.addSegment(segment)
        }
        return segment
    }

    override fun update(id: Long, element: ChainSegment): ChainSegment {
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

}