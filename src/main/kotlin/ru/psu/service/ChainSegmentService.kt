package ru.psu.service

import ru.psu.model.ChainSegment
import ru.psu.model.SegmentJoint

interface ChainSegmentService: ChainElementService<ChainSegment, SegmentJoint> {

    fun updateWithoutUpdateChild(id: Long, element: ChainSegment): ChainSegment
}