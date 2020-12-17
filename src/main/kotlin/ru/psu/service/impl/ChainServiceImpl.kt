package ru.psu.service.impl

import ru.psu.factory.impl.ChainElementServiceFactoryImpl
import ru.psu.model.Chain
import ru.psu.model.ChainElement
import ru.psu.model.ChainState
import ru.psu.model.Point
import ru.psu.service.ChainService


//TODO может быть проблема с конкуретным доступом к chain
class ChainServiceImpl : ChainService {
    private val chain: Chain = Chain()

    override fun getChain(): Chain {
        return chain
    }

    override fun calculateChainCenterMass(): Point {
        chain.rootElement?.let {
            val chainElementService = ChainElementServiceFactoryImpl.instance.create(it);

            val centerMass = chainElementService.calculateCenterMass(it)

            val weightPoint = centerMass.weightPoint

            return Point(weightPoint.x / centerMass.weight, weightPoint.y / centerMass.weight)
        }

        throw IllegalArgumentException("root segment not init yet, system does`t have system coordinates")
    }

    override fun addElement(chainElement: ChainElement, rootElement: ChainElement?): Chain {
        val chainElementService = ChainElementServiceFactoryImpl.instance.create(chainElement)

        if (chain.chainState == ChainState.INITIALIZED && rootElement == null) {
            throw IllegalArgumentException("Chain was initialized, root element must not be null")
        }

        val element = chainElementService.createElement(chainElement, rootElement)
        if (chain.chainState == ChainState.NOT_INITIALIZED) {
            this.chain.chainState = ChainState.INITIALIZED
            this.chain.rootElement = element
            this.chain.systemCoordinate = element.systemCoordinate
        }

        return chain
    }

    override fun deleteElement(chainElement: ChainElement): Chain {
        val chainElementService = ChainElementServiceFactoryImpl.instance.create(chainElement)

        if (this.chain.rootElement == chainElement) {
            this.chain.rootElement = null
            this.chain.chainState = ChainState.NOT_INITIALIZED
            this.chain.systemCoordinate = null
        }

        chainElementService.delete(chainElement)

        return chain
    }

    override fun updateElement(id: Long, chainElement: ChainElement): Chain {
        val chainElementService = ChainElementServiceFactoryImpl.instance.create(chainElement)

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