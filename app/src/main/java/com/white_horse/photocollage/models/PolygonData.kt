package com.white_horse.photocollage.models

import android.widget.FrameLayout

data class PolygonData(
    val pointList : List<Point>,
    val width : Float = 0f,
    val height : Float = 0f,
    val rect : RectData,
    val layoutParam : FrameLayout.LayoutParams
)