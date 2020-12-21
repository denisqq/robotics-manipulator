package ru.psu.service.validator.impl

import ru.psu.model.ChainSegment
import ru.psu.service.validator.ChainSegmentValidator
import java.lang.IllegalArgumentException

object ChainSegmentValidatorImpl : ChainSegmentValidator {
    override fun validate(chainSegment: ChainSegment) {
        val length = chainSegment.segmentLength()
        if(length > 250) {
            throw IllegalArgumentException("Length of segment can`t be greater than 250");
        }

        val systemCoordinate = chainSegment.systemCoordinate
        println(systemCoordinate)
        chainSegment.parentSegmentJoint?.let {
            if(it.maxAngle < systemCoordinate!!.angle) {
                throw IllegalArgumentException("Cannot rotate element on angle = ${systemCoordinate.angle} because segment joint max angle = ${it.maxAngle}")
            }
        }
    }
}