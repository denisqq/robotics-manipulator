package ru.psu.service.mapper

import org.mapstruct.Mapper
import ru.psu.model.Chain

@Mapper
interface ChainMapper: UpdateMapper<Chain> {

}