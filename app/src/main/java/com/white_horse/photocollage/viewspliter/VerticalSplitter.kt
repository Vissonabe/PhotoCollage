package com.white_horse.photocollage.viewspliter

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.white_horse.photocollage.models.Edge
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.PolygonSplit
import com.white_horse.photocollage.models.RectData

class VerticalSplitter(val tag: String, val context: Context) : ViewSplitter() {

    override suspend fun getPolygonSplit(
        p1: Point, p2: Point, parentRectData: RectData, edgeList: List<Edge>,
        firstIntersectEdge: Edge,
        secondIntersectEdge: Edge
    ): PolygonSplit? {

        return getPolygonSplitInternal(
            p1,
            p2,
            parentRectData,
            edgeList,
            firstIntersectEdge,
            secondIntersectEdge
        )
    }

    override fun getView1Params(
        parentRectData: RectData,
        v1RectData: RectData
    ): FrameLayout.LayoutParams {
        val isV1TopAligned = ((v1RectData.start_y - parentRectData.start_y) == 0f)
        val isV1RectLeftAligned = (v1RectData.start_x - parentRectData.start_x) == 0f
        val v1Param = FrameLayout.LayoutParams(
            (v1RectData.end_x - v1RectData.start_x).toInt(),
            (v1RectData.end_y - v1RectData.start_y).toInt()
        )
        val v1Gravity =
            (if (isV1RectLeftAligned) Gravity.START else Gravity.END) or (if (isV1TopAligned) Gravity.TOP else Gravity.BOTTOM)
        v1Param.gravity = v1Gravity
        return v1Param
    }

    override fun getView2Params(
        parentRectData: RectData,
        v2RectData: RectData
    ): FrameLayout.LayoutParams {
        val isV2TopAligned = ((v2RectData.start_y - parentRectData.start_y) == 0f)
        val isV2RectLeftAligned = (v2RectData.start_x - parentRectData.start_x) == 0f
        val v2Param = FrameLayout.LayoutParams(
            (v2RectData.end_x - v2RectData.start_x).toInt(),
            (v2RectData.end_y - v2RectData.start_y).toInt()
        )
        val v2Gravity =
            (if (isV2RectLeftAligned) Gravity.START else Gravity.END) or (if (isV2TopAligned) Gravity.TOP else Gravity.BOTTOM)
        v2Param.gravity = v2Gravity
        return v2Param
    }
}