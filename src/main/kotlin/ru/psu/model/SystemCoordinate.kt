package ru.psu.model

import ru.psu.model.enums.SystemCoordinateType

data class SystemCoordinate(var angle: Double, val systemCoordinateType: SystemCoordinateType = SystemCoordinateType.RIGHT)
