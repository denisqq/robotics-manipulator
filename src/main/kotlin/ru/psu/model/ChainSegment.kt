package ru.psu.model

data class ChainSegment(var id: Int?, val point: Point, val weight: Double, val children: MutableList<ChainSegment> = mutableListOf())