package ru.psu.model

import ru.psu.model.enums.SystemCoordinateType

data class SystemCoordinate(
    val angle: Double,
    val systemCoordinateType: SystemCoordinateType = SystemCoordinateType.RIGHT
)
