package ru.psu.service.impl

import ru.psu.model.ChainElement
import ru.psu.model.Point
import ru.psu.service.IntersectService

object IntersectServiceImpl: IntersectService {

    override fun findIntersect(vararg points: Point): Collection<ChainElement> {
        return ChainServiceImpl.instance.findElement(*points)
    }
}