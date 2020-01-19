package com.white_horse.photocollage.viewspliter

import android.widget.FrameLayout
import com.white_horse.photocollage.models.Edge
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.PolygonSplit
import com.white_horse.photocollage.models.RectData

interface IViewSplitter {
    suspend fun getPolygonSplit(p1 : Point, p2 : Point, rectData: RectData, edgeList: List<Edge>,
                        firstIntersectEdge: Edge,
                        secondIntersectEdge: Edge) : PolygonSplit?
    fun updateViewWidthHeight(width: Float, height : Float)
}