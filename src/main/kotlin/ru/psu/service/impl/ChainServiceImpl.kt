package ru.psu.service.impl

import ru.psu.factory.impl.ChainElementServiceFactoryImpl
import ru.psu.model.*
import ru.psu.service.ChainService
import java.lang.IllegalArgumentException

class ChainServiceImpl : ChainService {
    private val chain: Chain = Chain()

    override fun getChain(): Chain {
        return chain
    }

    override fun calculateChainCenterMass(): Point {
//        chain.rootChainSegment?.let {
//            return it.startPoint;
//        }

        throw IllegalArgumentException("root segment not init yet, system does`t have system coordinates")
    }

    override fun createElement(chainElement: ChainElement, rootElement: ChainElement): Chain {
        val chainElementService = ChainElementServiceFactoryImpl.instance.create(chainElement);

        val element = chainElementService.createElement(chainElement, rootElement)
        if(chain.chainState == ChainState.NOT_INITIALIZED) {
            this.chain.chainState = ChainState.INITIALIZED;
            this.chain.rootElement = element
            this.chain.systemCoordinate = element.systemCoordinate
        }

        return chain;
    }

//    override fun createElement(segment: ChainSegment): Chain {
//        TODO может быть проблема с конкуретным доступом к chain
//        if (chain.chainState == ChainState.NOT_INITIALIZED) {
//            val createdSegment = chainSegmentService.addChainSegment(segment)
//            chain.chainState = ChainState.INITIALIZED
//            chain.systemCoordinate = createdSegment.systemCoordinate
//        } else {
//            chainSegmentService.addChainSegment(segment)
//        }
//        return chain;
//    }

}