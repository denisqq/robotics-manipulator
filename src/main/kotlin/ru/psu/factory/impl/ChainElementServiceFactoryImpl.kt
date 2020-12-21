package ru.psu.factory.impl

import ru.psu.factory.ChainElementServiceFactory
import ru.psu.model.ChainElement
import ru.psu.model.enums.ChainElementType
import ru.psu.service.ChainElementService
import ru.psu.service.impl.ChainSegmentServiceImpl
import ru.psu.service.impl.SegmentJointService

class ChainElementServiceFactoryImpl<E : ChainElement, out S : ChainElementService<E, E>> private constructor() :
    ChainElementServiceFactory<E, S> {
    @Suppress("UNCHECKED_CAST")
    override fun create(element: ChainElement): S {
        return when (element.elementType) {
            ChainElementType.JOINT -> SegmentJointService.instance as S
            ChainElementType.SEGMENT -> ChainSegmentServiceImpl.instance as S
        }
    }

    private object HOLDER {
        val INSTANCE = ChainElementServiceFactoryImpl<ChainElement, ChainElementService<ChainElement, ChainElement>>()
    }

    companion object {
        val instance: ChainElementServiceFactoryImpl<ChainElement, ChainElementService<ChainElement, ChainElement>> by lazy { HOLDER.INSTANCE }
    }

}