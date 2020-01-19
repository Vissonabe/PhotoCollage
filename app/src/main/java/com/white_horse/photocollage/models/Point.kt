package com.white_horse.photocollage.models

data class Point(val x: Float,
                 val y: Float,
                 val rawX : Float = 0f,
                 val rawY : Float = 0f) {
    companion object {
        fun getDefaultPoint() = Point(0f, 0f)
        fun newPoint(x: Float, y: Float) =
            Point(x, y, rawX = x, rawY = y)
    }
}