package ru.psu.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import ru.psu.model.enums.ChainElementType


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes(JsonSubTypes.Type(value = ChainSegment::class), JsonSubTypes.Type(value = SegmentJoint::class))
interface ChainElement {
    var id: Long?
    var deleted: Boolean
    val elementType: ChainElementType
    var systemCoordinate: SystemCoordinate?
}