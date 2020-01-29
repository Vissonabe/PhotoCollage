package com.white_horse.photocollage.viewspliter

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.white_horse.photocollage.models.Edge
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.PolygonSplit
import com.white_horse.photocollage.models.RectData
import kotlin.math.abs

class DiagonalSplitter(val tag: String, val context: Context) : ViewSplitter() {

    override suspend fun getPolygonSplit(
        p1: Point,
        p2: Point,
        parentRectData: RectData,
        edgeList: List<Edge>,
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
        val isV1RectLeftAligned = ((v1RectData.start_x - parentRectData.start_x) == 0f)
        val v1Param = FrameLayout.LayoutParams(
            (v1RectData.end_x - v1RectData.start_x).toInt(),
            (v1RectData.end_y - v1RectData.start_y).toInt()
        )
        val v1Gravity =
            (if (isV1RectLeftAligned) Gravity.START else Gravity.END) or (if (isV1TopAligned) Gravity.TOP else Gravity.BOTTOM)
        v1Param.gravity = v1Gravity
        return v1Param // addViewMargin(v1Param, parentRectData, v1RectData)
    }

    fun addViewMargin(
        params: FrameLayout.LayoutParams,
        parentRectData: RectData,
        rectData: RectData
    ): FrameLayout.LayoutParams {
        params.topMargin = abs(parentRectData.start_y - rectData.start_y).toInt()
        params.bottomMargin = abs(parentRectData.end_y - rectData.end_y).toInt()
        params.leftMargin = abs(parentRectData.start_x - rectData.start_x).toInt()
        params.rightMargin = abs(parentRectData.end_x - rectData.end_x).toInt()
        return params
    }

    override fun getView2Params(
        parentRectData: RectData,
        v2RectData: RectData
    ): FrameLayout.LayoutParams {
        val isV2TopAligned = ((v2RectData.start_y - parentRectData.start_y) == 0f)
        val isV2RectLeftAligned = ((v2RectData.start_x - parentRectData.start_x) == 0f)
        val v2Param = FrameLayout.LayoutParams(
            (v2RectData.end_x - v2RectData.start_x).toInt(),
            (v2RectData.end_y - v2RectData.start_y).toInt()
        )
        val v2Gravity =
            (if (isV2RectLeftAligned) Gravity.START else Gravity.END) or (if (isV2TopAligned) Gravity.TOP else Gravity.BOTTOM)
        v2Param.gravity = v2Gravity
        return v2Param // addViewMargin(v2Param, parentRectData, v2RectData)
    }
}