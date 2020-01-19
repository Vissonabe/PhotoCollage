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

class VerticalSplitter(val tag : String) : IViewSplitter {
    var max = 0f
    var min = 0f
    var leftViewWidth = 0f
    var rightViewWidth = 0f
    var viewWidth = 0f
    var viewHeight = 0f

    override fun updateViewWidthHeight(width: Float, height: Float) {
        this.viewWidth = width
        this.viewHeight = height
    }

    private fun getV1Points(rectData: RectData, edgeList: List<Edge>,
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
                            abs(edge.intersectionPoint.rawY - rectData.start_y),
                            rawX = edge.start.rawX,
                            rawY = edge.start.rawY
                        )
                    )
                    list.add(
                        Point(
                            abs(rectData.start_x - edge.intersectionPoint.rawX),
                            abs(edge.intersectionPoint.rawY - rectData.start_y),
                            rawX = edge.intersectionPoint.rawX,
                            rawY = edge.intersectionPoint.rawY
                        )
                    )
                    list.add(
                        Point(
                            abs(rectData.start_x - secondIntersectEdge.intersectionPoint.rawX),
                            abs(secondIntersectEdge.intersectionPoint.rawY - rectData.start_y),
                            rawX = secondIntersectEdge.intersectionPoint.rawX,
                            rawY = secondIntersectEdge.intersectionPoint.rawY
                        )
                    )
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
                                abs(edge.intersectionPoint.rawY - rectData.start_y),
                                rawX = edge.intersectionPoint.rawX,
                                rawY = edge.intersectionPoint.rawY
                            )
                        )
                        break
                    } else {
                        v2PointsList.add(
                            Point(
                                abs(edge.intersectionPoint.rawX - rectData.start_x),
                                abs(edge.intersectionPoint.rawY - rectData.start_y),
                                rawX = edge.intersectionPoint.rawX,
                                rawY = edge.intersectionPoint.rawY
                            )
                        )
                        v2PointsList.add(
                            Point(
                                edge.end.rawX - rectData.start_x,
                                abs(edge.end.rawY - rectData.start_y),
                                rawX = edge.end.rawX,
                                rawY = edge.end.rawY
                            )
                        )
                    }
                } else {
                    v2PointsList.add(
                        Point(
                            edge.end.rawX - rectData.start_x,
                            abs(edge.end.rawY - rectData.start_y),
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
                        abs(edge.intersectionPoint.rawY),
                        rawX = edge.start.rawX,
                        rawY = edge.start.rawY
                    )
                )
                list.add(
                    Point(
                        abs(edge.intersectionPoint.rawX),
                        abs(edge.intersectionPoint.rawY),
                        rawX = edge.intersectionPoint.rawX,
                        rawY = edge.intersectionPoint.rawY
                    )
                )
                list.add(
                    Point(
                        abs(secondIntersectEdge.intersectionPoint.rawX),
                        abs(secondIntersectEdge.intersectionPoint.rawY),
                        rawX = secondIntersectEdge.intersectionPoint.rawX,
                        rawY = secondIntersectEdge.intersectionPoint.rawY
                    )
                )
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

    private suspend fun isPolygonLeftAligned(rectData: RectData, edgeList: List<Edge>,
                                             firstIntersectEdge: Edge,
                                             secondIntersectEdge: Edge) : Boolean {
        var isLeftAligned = false
        val leftEdge = RectData.getLeftEdge(rectData)
        Log.d("xxx temp", "xxx temp verti leftEdge $tag -- $leftEdge")
        val childEdges = createEdgesFromPoints(getTempPoints(edgeList, firstIntersectEdge, secondIntersectEdge))

        childEdges.forEach {
            Log.d("xxx temp", "xxx temp verti $tag -- $it")
        }

        childEdges.forEach {
            val intersection = findIntersection(leftEdge.start, leftEdge.end, it.start, it.end)
            if(intersection != null && intersection.x >= 0 && intersection.y >= 0){
                Log.d("xxx temp", "xxx temp interscetion $tag -- $intersection")
                isLeftAligned = true
            }
        }
        Log.d("xxx temp", "xxx temp is left aligned $tag -- $isLeftAligned")
        return isLeftAligned
    }

    override suspend fun getPolygonSplit(p1 : Point, p2 : Point, rectData: RectData, edgeList: List<Edge>,
                                 firstIntersectEdge: Edge,
                                 secondIntersectEdge: Edge): PolygonSplit? {
        caculateMinMax(p1, p2, rectData)
        val leftRect = RectData(
            rectData.start_x,
            max,
            rectData.start_y,
            rectData.end_y
        )
        val rightRect = RectData(
            min,
            rectData.end_x,
            rectData.start_y,
            rectData.end_y
        )

        val isLeftAligned = isPolygonLeftAligned(rectData, edgeList, firstIntersectEdge, secondIntersectEdge)

        Log.d("xxx", "getPolygonSplit vertical $tag -- $isLeftAligned")
//        Log.d("xxx", "getPolygonSplit ${Thread.currentThread().name}")

        val v1Rect = if(isLeftAligned) leftRect else rightRect
        val v2Rect = if(isLeftAligned) rightRect else leftRect
        val v1PointsList = getV1Points(v1Rect, edgeList, firstIntersectEdge, secondIntersectEdge)
        val v2PointsList = getV2Points(v2Rect, edgeList, firstIntersectEdge, secondIntersectEdge)
        val v1Width = if(isLeftAligned) leftViewWidth else rightViewWidth
        val v2Width = if(isLeftAligned) rightViewWidth else leftViewWidth

        Log.d("xxx", "v1 vertical intersection point: $v1PointsList -------- rect data $leftRect")
        Log.d("xxx", "v2 vertical intersection point: $v2PointsList -------- rect data $leftRect")

        val paramsPair = getLayoutParamsPair(leftViewWidth.toInt(), rightViewWidth.toInt())
        val v1Param = if(isLeftAligned) paramsPair.first else paramsPair.second
        val v2Param = if(isLeftAligned) paramsPair.second else paramsPair.first

        return PolygonSplit(
            Split.VERTICAL,
            PolygonData(v1PointsList, v1Width, viewHeight, v1Rect, v1Param),
            PolygonData(v2PointsList, v2Width, viewHeight, v2Rect, v2Param)
        )
    }

    fun getLayoutParamsPair(leftViewWidth : Int, rightViewWidth : Int): Pair<FrameLayout.LayoutParams, FrameLayout.LayoutParams> {
        val lp1 =
            FrameLayout.LayoutParams(
                leftViewWidth,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        lp1.gravity = Gravity.START

        val lp2 =
            FrameLayout.LayoutParams(
                rightViewWidth,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        lp2.gravity = Gravity.END
        return Pair(lp1, lp2)
    }

    fun caculateMinMax(d_dash : Point, u_dash : Point, rectData: RectData) {
        max = max(d_dash.rawX, u_dash.rawX)
        min =  min(d_dash.x, u_dash.x)
        leftViewWidth = abs(max - rectData.start_x)
        rightViewWidth = abs(rectData.end_x - min)
        Log.d("xxx vertical", "v1width = $leftViewWidth ,  v2width = $rightViewWidth")
    }
}