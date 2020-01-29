package com.white_horse.photocollage.viewspliter

import android.content.Context
import com.white_horse.photocollage.models.Edge
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.RectData
import com.white_horse.photocollage.models.Split
import com.white_horse.photocollage.utils.LogTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.sqrt

fun getRectData(points : List<Point>) : RectData {
    val point = points[0]
    var minX = point.rawX
    var maxX = point.rawX
    var minY = point.rawY
    var maxY = point.rawY

    points.forEach {
        if(it.rawX < minX){
            minX = it.rawX
        }

        if(it.rawX > maxX){
            maxX = it.rawX
        }

        if(it.rawY < minY){
            minY = it.rawY
        }

        if(it.rawY > maxY){
            maxY = it.rawY
        }
    }

    return RectData(minX, maxX, minY, maxY)
}

fun createEdgesFromPoints(points: List<Point>) : List<Edge>{
    val edgeList = ArrayList<Edge>()
    for (i in points.indices) {
        var endIndex = i + 1
        endIndex = if (endIndex == points.size) 0 else endIndex
        edgeList.add(
            Edge(
                i,
                points[i],
                points[endIndex]
            )
        )
    }
    return edgeList
}

suspend fun getSplitDirection(context: Context, start : Point, end : Point) : Split {
    return splitDirection(context, start, end)
}

fun isSplitHorizontal(start : Point, end : Point) : Boolean {
    return abs(start.rawX - end.rawX) > abs(start.rawY - end.rawY)
}

suspend fun splitDirection(context: Context, lineStart : Point, lineEnd : Point): Split {
    val deviceHeight = context.resources.displayMetrics.heightPixels.toFloat()
    val deviceWidth = context.resources.displayMetrics.widthPixels.toFloat()
    val leftEdge = Edge(
        0,
        Point(0f, 0f),
        Point(0f, deviceHeight)
    )
    val topEdge = Edge(
        1,
        Point(0f, 0f),
        Point(deviceWidth, 0f)
    )
    val rightEdge = Edge(
        2,
        Point(deviceWidth, 0f),
        Point(deviceWidth, deviceHeight)
    )
    val bottomEdge = Edge(
        3,
        Point(0f, deviceHeight),
        Point(deviceWidth, deviceHeight)
    )
    val leftIntersection = findIntersection(
        leftEdge.start,
        leftEdge.end,
        lineStart,
        lineEnd
    )
    val topIntersection = findIntersection(
        topEdge.start,
        topEdge.end,
        lineStart,
        lineEnd
    )
    val rightIntersection = findIntersection(
        rightEdge.start,
        rightEdge.end,
        lineStart,
        lineEnd
    )
    val bottomIntersection = findIntersection(
        bottomEdge.start,
        bottomEdge.end,
        lineStart,
        lineEnd
    )

    LogTrace.d("left - $leftIntersection, top - $topIntersection, right - $rightIntersection, bottom - $bottomIntersection")

    return if(leftIntersection != null && rightIntersection != null){
        Split.HORIZONTAL
    } else if(topIntersection != null && bottomIntersection != null) {
        Split.VERTICAL
    } else if((topIntersection != null && (leftIntersection != null || rightIntersection != null)) ||
        (bottomIntersection != null && (leftIntersection != null || rightIntersection != null))) {
        Split.DIAGONAL
    } else {
        Split.UNKNOWN
    }
}

suspend fun findIntersection(p1: Point, p2: Point, p3: Point, p4: Point): Point? {
    return withContext(Dispatchers.Default) {
        var xD1: Float = p2.x - p1.x
        var yD1: Float = p2.y - p1.y
        var xD2: Float = p4.x - p3.x
        var yD2: Float = p4.y - p3.y
        val xD3: Float = p1.x - p3.x
        val yD3: Float = p1.y - p3.y
        val dot: Float
        val deg: Float
        val len1: Float
        val len2: Float
        val segmentLen1: Float
        val segmentLen2: Float
        val ua: Float
        val ub: Float
        val div: Float

        // calculate differences

        // calculate the lengths of the two lines
        len1 = sqrt(xD1 * xD1 + yD1 * yD1)
        len2 = sqrt(xD2 * xD2 + yD2 * yD2)

        // calculate angle between the two lines.
        dot = xD1 * xD2 + yD1 * yD2 // dot product
        deg = dot / (len1 * len2)

        // if abs(angle)==1 then the lines are parallell,
        // so no intersection is possible
        if (abs(deg) == 1f) {
            null
        }

        // find intersection Pt between two lines

        div = yD2 * xD1 - xD2 * yD1
        ua = (xD2 * yD3 - yD2 * xD3) / div
        ub = (xD1 * yD3 - yD1 * xD3) / div
        val x = p1.x + ua * xD1
        val y = p1.y + ua * yD1
        val pt =
            Point(x, y, rawX = x, rawY = y)

        // calculate the combined length of the two segments
        // between Pt-p1 and Pt-p2
        xD1 = pt.x - p1.x
        xD2 = pt.x - p2.x
        yD1 = pt.y - p1.y
        yD2 = pt.y - p2.y
        segmentLen1 = sqrt(xD1 * xD1 + yD1 * yD1) + sqrt(xD2 * xD2 + yD2 * yD2)

        // calculate the combined length of the two segments
        // between Pt-p3 and Pt-p4
        xD1 = pt.x - p3.x
        xD2 = pt.x - p4.x
        yD1 = pt.y - p3.y
        yD2 = pt.y - p4.y
        segmentLen2 = sqrt(xD1 * xD1 + yD1 * yD1) + sqrt(xD2 * xD2 + yD2 * yD2)

        // if the lengths of both sets of segments are the same as
        // the lenghts of the two lines the point is actually
        // on the line segment.

        // if the point isn’t on the line, return null
        if (abs(len1 - segmentLen1) > 0.01 || abs(len2 - segmentLen2) > 0.01) null else pt

        // return the valid intersection
    }
}