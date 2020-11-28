package ru.psu.model

data class ChainSegment(var id: Int?,
                        val startPoint: Point,
                        val endPoint: Point,
                        val weight: Double,
                        val children: MutableList<ChainSegment> = mutableListOf(),
                        val segmentType: ChainSegmentType = ChainSegmentType.REGULAR,
                        var parent: ChainSegment? = null
) {

    fun getSegmentLength() {
        //TODO not implemented yet
    }
}