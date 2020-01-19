package com.white_horse.photocollage.models

data class RectData(val start_x : Float, val end_x : Float, val start_y : Float, val end_y : Float){
    companion object {
        fun getDefaultRect() =
            RectData(0f, 0f, 0f, 0f)

        fun getTopEdge(rectData: RectData) : Edge {
            return Edge(0, Point(rectData.start_x, rectData.start_y), Point(rectData.end_x, rectData.start_y))
        }

        fun getLeftEdge(rectData: RectData) : Edge {
            return Edge(0, Point(rectData.start_x, rectData.start_y), Point(rectData.start_x, rectData.end_y))
        }
    }
}