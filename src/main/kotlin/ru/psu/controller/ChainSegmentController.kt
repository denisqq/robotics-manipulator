package ru.psu.controller

import ru.psu.model.Point

interface ChainSegmentController {
    fun calculateSegmentCenterMass() : Point
}