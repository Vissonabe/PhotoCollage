package com.white_horse.photocollage.utils

import android.content.Context
import android.graphics.Path
import android.graphics.Rect
import com.white_horse.photocollage.models.Edge
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.RectData
import com.white_horse.photocollage.models.Split
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.sqrt

fun Path.moveTo(point : Point) {
    moveTo(point.x, point.y)
}

fun Path.lineTo(point : Point) {
    lineTo(point.x, point.y)
}

fun dpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}

suspend fun findVirtualEndPoint(down : Point, up : Point, multiplyFactor : Int = 1) : Pair<Point, Point> {
    return withContext(Dispatchers.Default) {
        val endX = (up.x + ((up.x - down.x) * multiplyFactor))
        val endY = (up.y - ((down.y - up.y) * multiplyFactor))
        val endPoint = Point.newPoint(endX, endY)

        val startX = (down.x - ((up.x - down.x) * multiplyFactor))
        val startY = (down.y + ((down.y - up.y) * multiplyFactor))
        val startPoint = Point.newPoint(startX, startY)

        Pair(startPoint, endPoint)
    }
}

fun <E> MutableList<E>.clearAndAdd(elements: Collection<E>){
    clear()
    addAll(elements)
}

fun concatString(v1 : String, v2 : String) : String {
    return "$v1$v2"
}

fun getLineLength(x1 : Float, y1 : Float, x2 : Float, y2 : Float): Float = sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))