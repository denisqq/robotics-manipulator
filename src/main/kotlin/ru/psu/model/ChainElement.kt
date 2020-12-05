package ru.psu.model

import ru.psu.model.enums.ChainElementType

interface ChainElement {
    var id: Long?
    val elementType: ChainElementType
    val systemCoordinate: SystemCoordinate
}