package ru.psu.service

import ru.psu.model.ChainElement
import ru.psu.model.Point

interface IntersectService {
    fun findIntersect(vararg points: Point): Collection<ChainElement>
}