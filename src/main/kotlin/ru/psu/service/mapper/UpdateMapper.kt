package ru.psu.service.mapper

import org.mapstruct.MappingTarget
import ru.psu.model.ChainSegment

interface UpdateMapper<T> {
    fun update(from: T, @MappingTarget to: T)
}