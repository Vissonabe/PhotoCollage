package com.white_horse.photocollage.view.guideline

import android.util.Log
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.utils.getLineLength

class GuideLineViewManager(val lineAction : (Pair<Point, Point>) -> Unit) {

    val multiplyFactor = 4
    val THRESHOLD_LENGTH = 300f

    fun calculateLineLength(downX : Float, downY : Float, upX : Float, upY : Float) {
        val lineLength = getLineLength(downX, downY, upX, upY)
        Log.d("xxx", "length of line $lineLength")
        if (isLineGreaterThanThreshold(lineLength)) {
            findVirtualEndPoint(downX, downY, upX, upY)
        }
    }

    private fun findVirtualEndPoint(downX : Float, downY : Float, upX : Float, upY : Float) {
        val endX = (upX + ((upX - downX) * multiplyFactor))
        val endY = (upY - ((downY - upY) * multiplyFactor))
        val virtualEndPoint = Point.newPoint(endX, endY)

        val startX = (downX - ((upX - downX) * multiplyFactor))
        val startY = (downY + ((downY - upY) * multiplyFactor))

        val virtualStartPoint = Point.newPoint(startX, startY)
        lineAction.invoke(Pair(virtualStartPoint, virtualEndPoint))
    }

    fun isLineGreaterThanThreshold(length : Float): Boolean {
        return length > THRESHOLD_LENGTH
    }

    fun isLineGreaterThanThreshold(downX : Float, downY : Float, upX : Float, upY : Float): Boolean {
        val lineLength = getLineLength(downX, downY, upX, upY)
        return lineLength > THRESHOLD_LENGTH
    }
}