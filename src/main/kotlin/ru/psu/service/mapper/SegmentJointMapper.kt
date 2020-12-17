package ru.psu.service.mapper

import org.mapstruct.*
import ru.psu.model.SegmentJoint

@Mapper
interface SegmentJointMapper : ElementUpdateMapper<SegmentJoint> {

    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(source = "weight", target = "weight"),
        Mapping(source = "maxAngle", target = "maxAngle"),
//        Mapping(source = "systemCoordinate", target = "systemCoordinate")
    )
    override fun update(from: SegmentJoint, @MappingTarget to: SegmentJoint)
}