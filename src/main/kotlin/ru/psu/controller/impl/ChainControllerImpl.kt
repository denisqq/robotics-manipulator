package ru.psu.controller.impl

import ru.psu.controller.ChainController
import ru.psu.model.Chain
import ru.psu.model.ChainElement
import ru.psu.service.ChainService
import ru.psu.service.impl.ChainServiceImpl

class ChainControllerImpl : ChainController {
    private val chainService: ChainService = ChainServiceImpl.instance


    override fun getChain(): Chain {
        return chainService.getChain()
    }

    override fun addChainElement(chainElement: ChainElement, rootElement: ChainElement?): Chain {
        return chainService.addElement(chainElement, rootElement)
    }

    override fun deleteChainElement(chainElement: ChainElement): Chain {
        return chainService.deleteElement(chainElement)
    }

    override fun updateChainElement(id: Long, chainElement: ChainElement): Chain {
        return chainService.updateElement(id, chainElement)
    }
}