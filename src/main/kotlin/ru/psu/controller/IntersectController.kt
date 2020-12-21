package ru.psu.controller

import ru.psu.model.ChainElement
import ru.psu.model.Point

interface IntersectController {
    fun findIntersect(vararg points: Point): Collection<ChainElement>
}