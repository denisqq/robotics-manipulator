package ru.psu.controller.impl

import ru.psu.controller.IntersectController
import ru.psu.model.ChainElement
import ru.psu.model.Point
import ru.psu.service.impl.IntersectServiceImpl

object IntersectControllerImpl : IntersectController {

    override fun findIntersect(vararg points: Point): Collection<ChainElement> {
        return IntersectServiceImpl.findIntersect(*points);
    }
}