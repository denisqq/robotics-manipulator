package ru.psu.service.impl

import org.mapstruct.factory.Mappers
import ru.psu.factory.impl.ChainElementServiceFactoryImpl
import ru.psu.model.*
import ru.psu.service.ChainService
import ru.psu.service.mapper.ChainSegmentMapper
import java.lang.IllegalArgumentException


//TODO может быть проблема с конкуретным доступом к chain
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

    override fun addElement(chainElement: ChainElement, rootElement: ChainElement): Chain {
        val chainElementService = ChainElementServiceFactoryImpl.instance.create(chainElement);

        val element = chainElementService.createElement(chainElement, rootElement)
        if(chain.chainState == ChainState.NOT_INITIALIZED) {
            this.chain.chainState = ChainState.INITIALIZED;
            this.chain.rootElement = element
            this.chain.systemCoordinate = element.systemCoordinate
        }

        return chain;
    }

    override fun deleteElement(chainElement: ChainElement): Chain {
        val chainElementService = ChainElementServiceFactoryImpl.instance.create(chainElement);

        chainElementService.delete(chainElement)

        return chain
    }

    override fun updateElement(id: Long, chainElement: ChainElement): Chain {
        val chainElementService = ChainElementServiceFactoryImpl.instance.create(chainElement);

        chainElementService.update(id, chainElement)

        return chain
    }


    private object HOLDER {
        val INSTANCE = ChainServiceImpl()
    }

    companion object {
        val instance: ChainService by lazy { HOLDER.INSTANCE }
    }
}