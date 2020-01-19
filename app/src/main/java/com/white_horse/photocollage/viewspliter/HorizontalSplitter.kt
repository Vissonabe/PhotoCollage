package com.white_horse.photocollage.viewspliter

import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.white_horse.photocollage.models.*
import com.white_horse.photocollage.utils.createEdgesFromPoints
import com.white_horse.photocollage.utils.findIntersection
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class HorizontalSplitter(val tag : String): IViewSplitter {

    var max = 0f
    var min = 0f
    var topViewHeight = 0f
    var bottomViewHeight = 0f
    var viewWidth = 0f
    var viewHeight = 0f

    private fun getV1Points(rectData: RectData,
                            edgeList: List<Edge>,
                            firstIntersectEdge: Edge,
                            secondIntersectEdge: Edge): List<Point> {
        val list = mutableListOf<Point>()
        var index = 0
        while (index < edgeList.size) {
            val edge = edgeList[index]
            if (edge.hasIntersection) {
                list.add(
                    Point(
                        abs(edge.start.rawX - rectData.start_x),
                        abs(edge.start.rawY - rectData.start_y),
                        rawX = edge.start.rawX,
                        rawY = edge.start.rawY
                    )
                )
                list.add(
                    Point(
                        abs(edge.intersectionPoint.x - rectData.start_x),
                        abs(rectData.start_y - edge.intersectionPoint.rawY),
                        rawX = edge.intersectionPoint.rawX,
                        rawY = edge.intersectionPoint.rawY
                    )
                )
                list.add(
                    Point(
                        abs(secondIntersectEdge.intersectionPoint.x - rectData.start_x),
                        abs(rectData.start_y - secondIntersectEdge.intersectionPoint.rawY),
                        rawX = secondIntersectEdge.intersectionPoint.rawX,
                        rawY = secondIntersectEdge.intersectionPoint.rawY
                    )
                ) //(edge.intersectionPoint)
                index = secondIntersectEdge.index + 1
            } else {
                list.add(
                    Point(
                        abs(edge.start.rawX - rectData.start_x),
                        abs(edge.start.rawY - rectData.start_y),
                        rawX = edge.start.rawX,
                        rawY = edge.start.rawY
                    )
                )
                index++
            }
        }
        return list
    }

    private fun getV2Points(rectData: RectData, edgeList: List<Edge>,
                            firstIntersectEdge: Edge,
                            secondIntersectEdge: Edge): List<Point> {
        val v2PointsList = mutableListOf<Point>()
        var index = firstIntersectEdge.index

        while (index < edgeList.size) {
            val edge = edgeList[index]
            if (edge.hasIntersection) {
                if (edge.index == secondIntersectEdge.index) {
                    v2PointsList.add(
                        Point(
                            abs(edge.intersectionPoint.rawX - rectData.start_x),
                            abs(rectData.start_y - edge.intersectionPoint.rawY),
                            rawX = edge.intersectionPoint.rawX,
                            rawY = edge.intersectionPoint.rawY
                        )
                    )
                    break
                } else {
                    v2PointsList.add(
                        Point(
                            abs(edge.intersectionPoint.rawX - rectData.start_x),
                            abs(rectData.start_y - edge.intersectionPoint.rawY),
                            rawX = edge.intersectionPoint.rawX,
                            rawY = edge.intersectionPoint.rawY
                        )
                    )
                    v2PointsList.add(
                        Point(
                            abs(edge.end.rawX - rectData.start_x),
                            abs(rectData.start_y - edge.end.rawY),
                            rawX = edge.end.rawX,
                            rawY = edge.end.rawY
                        )
                    )
                }
            } else {
                v2PointsList.add(
                    Point(
                        abs(edge.end.rawX - rectData.start_x),
                        abs(rectData.start_y - edge.end.rawY),
                        rawX = edge.end.rawX,
                        rawY = edge.end.rawY
                    )
                )
            }
            index++
        }

        return v2PointsList
    }

    private fun getTempPoints(edgeList: List<Edge>,
                              firstIntersectEdge: Edge,
                              secondIntersectEdge: Edge): List<Point> {
        val list = mutableListOf<Point>()
        var index = 0
        while (index < edgeList.size) {
            val edge = edgeList[index]
            if (edge.hasIntersection) {
                list.add(
                    Point(
                        abs(edge.start.rawX),
                        abs(edge.start.rawY),
                        rawX = edge.start.rawX,
                        rawY = edge.start.rawY
                    )
                )
                list.add(
                    Point(
                        abs(edge.intersectionPoint.x),
                        abs(edge.intersectionPoint.rawY),
                        rawX = edge.intersectionPoint.rawX,
                        rawY = edge.intersectionPoint.rawY
                    )
                )
                list.add(
                    Point(
                        abs(secondIntersectEdge.intersectionPoint.x),
                        abs(secondIntersectEdge.intersectionPoint.rawY),
                        rawX = secondIntersectEdge.intersectionPoint.rawX,
                        rawY = secondIntersectEdge.intersectionPoint.rawY
                    )
                ) //(edge.intersectionPoint)
                index = secondIntersectEdge.index + 1
            } else {
                list.add(
                    Point(
                        abs(edge.start.rawX),
                        abs(edge.start.rawY),
                        rawX = edge.start.rawX,
                        rawY = edge.start.rawY
                    )
                )
                index++
            }
        }
        return list
    }

    private suspend fun isPolygonTopAligned(rectData: RectData, edgeList: List<Edge>,
                                            firstIntersectEdge: Edge,
                                            secondIntersectEdge: Edge) : Boolean {
        var isTopAligned = false
        val topEdge = RectData.getTopEdge(rectData)
        Log.d("xxx temp", "xxx temp hori topEdge $tag -- $topEdge")
        val childEdges = createEdgesFromPoints(getTempPoints(edgeList, firstIntersectEdge, secondIntersectEdge))

        childEdges.forEach {
            Log.d("xxx temp", "xxx temp hori $tag -- $it")
        }

        childEdges.forEach {
            val intersection = findIntersection(topEdge.start, topEdge.end, it.start, it.end)
            if(intersection != null && intersection.x >= 0 && intersection.y >= 0) {
                Log.d("xxx temp", "xxx temp interscetion $tag -- $intersection")
                isTopAligned = true
            }
        }
        Log.d("xxx temp", "xxx temp is top aligned $tag -- $isTopAligned")
        return isTopAligned
    }

    override suspend fun getPolygonSplit(p1: Point, p2: Point, rectData: RectData, edgeList: List<Edge>,
                                 firstIntersectEdge: Edge,
                                 secondIntersectEdge: Edge): PolygonSplit? {
        Log.d("xxx", "parent rect data $rectData")

        calculateMinMax(p1, p2, rectData)
        val topRect = RectData(
            rectData.start_x,
            rectData.end_x,
            rectData.start_y,
            max
        )
        val bottomRect = RectData(
            rectData.start_x,
            rectData.end_x,
            min,
            rectData.end_y
        )

        val isTopAligned = isPolygonTopAligned(rectData, edgeList, firstIntersectEdge, secondIntersectEdge)
        Log.d("xxx", "getPolygonSplit horizontal $tag -- $isTopAligned")
//        Log.d("xxx", "getPolygonSplit ${Thread.currentThread().name}")

        val v1Rect = if(isTopAligned) topRect else bottomRect
        val v2Rect = if(isTopAligned) bottomRect else topRect
        val v1PointsList = getV1Points(v1Rect, edgeList, firstIntersectEdge, secondIntersectEdge)
        val v2PointsList = getV2Points(v2Rect, edgeList, firstIntersectEdge, secondIntersectEdge)
        val v1Height = if(isTopAligned) topViewHeight else bottomViewHeight
        val v2Height = if(isTopAligned) bottomViewHeight else topViewHeight

        Log.d("xxx", "v1 hori intersection point: $v1PointsList -------- rect r1 data $topRect")
        Log.d("xxx", "v2 hori intersection point: $v2PointsList -------- rect r2 data $topRect")

        val paramsPair = getLayoutParamsPair(topViewHeight.toInt(), bottomViewHeight.toInt())
        val v1Param = if(isTopAligned) paramsPair.first else paramsPair.second
        val v2Param = if(isTopAligned) paramsPair.second else paramsPair.first

        return PolygonSplit(
            Split.HORIZONTAL,
            PolygonData(v1PointsList, viewWidth, v1Height, v1Rect, v1Param),
            PolygonData(v2PointsList, viewWidth, v2Height, v2Rect, v2Param)
        )
    }

    private fun getLayoutParamsPair(topViewHeight : Int, bottomViewHeight : Int): Pair<FrameLayout.LayoutParams, FrameLayout.LayoutParams> {
        val lp1 = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            topViewHeight
        )
        lp1.gravity = Gravity.TOP

        val lp2 = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            bottomViewHeight
        )
        lp2.gravity = Gravity.BOTTOM
        return Pair(lp1, lp2)
    }

    override fun updateViewWidthHeight(width: Float, height: Float) {
        this.viewWidth = width
        this.viewHeight = height
    }

    fun calculateMinMax(d_dash: Point, u_dash: Point, rectData: RectData) {
        max = max(d_dash.rawY, u_dash.rawY)
        min = min(d_dash.rawY, u_dash.rawY)
        topViewHeight = abs(max - rectData.start_y)
        bottomViewHeight = abs(rectData.end_y - min)
        Log.d("xxx vertical", "max = $max, min = $min, v1width = $viewHeight ,  v2width = $bottomViewHeight")
    }
}