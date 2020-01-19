package com.white_horse.photocollage.models

data class Edge(
    val index: Int,
    val start: Point,
    val end: Point,
    var hasIntersection: Boolean = false,
    var intersectionPoint: Point = Point.getDefaultPoint()
) {
    companion object {
        fun getDefaultEdge() = Edge(
            -1,
            Point.getDefaultPoint(),
            Point.getDefaultPoint()
        )
        fun isDefaultEdge(edge: Edge) = edge.index == -1
    }
}