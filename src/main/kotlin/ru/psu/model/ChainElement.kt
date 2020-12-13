package ru.psu.model

import ru.psu.model.enums.ChainElementType

interface ChainElement {
    var id: Long?
    var deleted: Boolean
    val elementType: ChainElementType
    var systemCoordinate: SystemCoordinate
}