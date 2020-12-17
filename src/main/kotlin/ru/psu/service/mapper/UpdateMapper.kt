package ru.psu.service.mapper

import org.mapstruct.MappingTarget

interface UpdateMapper<T> {
    fun update(from: T, @MappingTarget to: T)
}