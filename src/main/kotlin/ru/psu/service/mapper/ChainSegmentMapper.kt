package ru.psu.service.mapper

import org.mapstruct.*
import ru.psu.model.ChainSegment

@Mapper
interface ChainSegmentMapper: ElementUpdateMapper<ChainSegment> {

    @BeanMapping(ignoreByDefault = true)
    @Mappings(
        Mapping(source = "hidden", target = "hidden"),
        Mapping(source = "weight", target = "weight"),
        Mapping(source = "ephemeral", target = "ephemeral")
    )
    override fun update(from: ChainSegment, @MappingTarget to: ChainSegment)
}