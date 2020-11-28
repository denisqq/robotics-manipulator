package ru.psu.model

data class Point(val x: Double, val y: Double) {

    fun concat (point: Point) : Point {
        return Point(x = this.x + point.x, y = this.y + point.y)
    }
}