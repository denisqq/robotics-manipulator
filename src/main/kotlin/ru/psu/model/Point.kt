package ru.psu.model

data class Point(var x: Double, var y: Double) {

    fun concat(point: Point): Point {
        return Point(x = this.x + point.x, y = this.y + point.y)
    }
}