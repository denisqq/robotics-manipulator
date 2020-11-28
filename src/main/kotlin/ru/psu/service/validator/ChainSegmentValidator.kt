package ru.psu.service.validator

import ru.psu.model.ChainSegment

interface ChainSegmentValidator {
    fun validate(chainSegment: ChainSegment)
}