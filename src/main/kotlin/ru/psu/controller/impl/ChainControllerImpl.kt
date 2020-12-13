package ru.psu.controller.impl

import ru.psu.controller.ChainController
import ru.psu.model.Chain
import ru.psu.model.ChainSegment
import ru.psu.model.Point
import ru.psu.service.ChainService
import ru.psu.service.impl.ChainServiceImpl

class ChainControllerImpl : ChainController {
    val chainService: ChainService = ChainServiceImpl.instance;


    override fun getChain(): Chain {
        return chainService.getChain()
    }

    override fun addChainSegment() {

        //Here come props for chain segment
//        ChainSegment(point = Point(x ))
        TODO("Not yet implemented")
    }
}