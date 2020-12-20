package ru.psu.service.validator

import ru.psu.model.SegmentJoint

interface SegmentJointValidator {
    fun validate(segmentJoint: SegmentJoint)
}