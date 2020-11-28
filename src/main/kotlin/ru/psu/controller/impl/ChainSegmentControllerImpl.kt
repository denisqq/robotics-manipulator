package ru.psu.controller.impl

import ru.psu.controller.ChainSegmentController
import ru.psu.model.Point
import ru.psu.service.ChainSegmentService
import ru.psu.service.impl.ChainSegmentServiceImpl

class ChainSegmentControllerImpl : ChainSegmentController {
    //todo remove this, need DI or Singleton object
    private val chainSegmentService: ChainSegmentService = ChainSegmentServiceImpl();

    override fun calculateSegmentCenterMass(): Point {
        TODO("Not yet implemented")
    }
}