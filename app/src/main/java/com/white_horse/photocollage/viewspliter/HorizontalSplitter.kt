package com.white_horse.photocollage.viewspliter

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.white_horse.photocollage.models.RectData
import com.white_horse.photocollage.utils.LogTrace

class HorizontalSplitter(val getTagAction: () -> String, val context: Context): ViewSplitter() {

    override fun getParentViewTag(): String {
        return getTagAction.invoke()
    }

    override fun getView1Params(
        parentRectData: RectData,
        v1RectData: RectData
    ): FrameLayout.LayoutParams {
        val v1Param = FrameLayout.LayoutParams((v1RectData.end_x - v1RectData.start_x).toInt(),
            (v1RectData.end_y - v1RectData.start_y).toInt())
        return addViewMargin(v1Param, parentRectData, v1RectData)
    }

    override fun getView2Params(
        parentRectData: RectData,
        v2RectData: RectData
    ): FrameLayout.LayoutParams {
        val v2Param = FrameLayout.LayoutParams((v2RectData.end_x - v2RectData.start_x).toInt(),
            (v2RectData.end_y - v2RectData.start_y).toInt())
        return addViewMargin(v2Param, parentRectData, v2RectData)
    }
}