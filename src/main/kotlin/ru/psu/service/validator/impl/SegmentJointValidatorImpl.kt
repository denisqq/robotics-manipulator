package ru.psu.service.validator.impl

import ru.psu.model.SegmentJoint
import ru.psu.service.validator.SegmentJointValidator

object SegmentJointValidatorImpl : SegmentJointValidator {
    override fun validate(segmentJoint: SegmentJoint) {
//        segmentJoint.maxAngle > 90
    }
}