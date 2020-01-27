package com.white_horse.photocollage.viewspliter

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.white_horse.photocollage.models.*
import com.white_horse.photocollage.utils.dpToPx
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class VerticalSplitter(val tag : String, val context: Context) : IViewSplitter {
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
                            secondIntersectEdge: Edge): List<Point>? {
            val list = mutableListOf<Point>()
            var index = 0

        if(edgeList.isNotEmpty()) {
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
        }else {
            return null
        }
    }

    private fun getV2Points(rectData: RectData, edgeList: List<Edge>,
                            firstIntersectEdge: Edge,
                            secondIntersectEdge: Edge): List<Point>? {
            val v2PointsList = mutableListOf<Point>()
            var index = firstIntersectEdge.index

        if(index >= 0) {
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
        } else {
            return null
        }
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

//        childEdges.forEach {
//            Log.d("xxx temp", "xxx temp verti $tag -- $it")
//        }

        childEdges.forEach {
            val intersection = findIntersection(leftEdge.start, leftEdge.end, it.start, it.end)
            if(intersection != null && intersection.x >= 0 && intersection.y >= 0){
//                Log.d("xxx temp", "xxx temp interscetion $tag -- $intersection")
                isLeftAligned = true
            }
        }
        Log.d("xxx temp", "xxx temp is left aligned $tag -- $isLeftAligned")
        return isLeftAligned
    }

    override suspend fun getPolygonSplit(p1 : Point, p2 : Point, parentRectData: RectData, edgeList: List<Edge>,
                                         firstIntersectEdge: Edge,
                                         secondIntersectEdge: Edge): PolygonSplit? {

        if(edgeList.isEmpty()) {
             return null
        }

        caculateMinMax(p1, p2, parentRectData)
        val leftRect = RectData(
            parentRectData.start_x,
            max,
            parentRectData.start_y,
            parentRectData.end_y
        )
        val rightRect = RectData(
            min,
            parentRectData.end_x,
            parentRectData.start_y,
            parentRectData.end_y
        )

        val isLeftAligned = isPolygonLeftAligned(parentRectData, edgeList, firstIntersectEdge, secondIntersectEdge)

        Log.d("xxx", "getPolygonSplit vertical $tag -- $isLeftAligned")
//        Log.d("xxx", "getPolygonSplit ${Thread.currentThread().name}")

        val v1Rect = if(isLeftAligned) leftRect else rightRect
        val v2Rect = if(isLeftAligned) rightRect else leftRect
        val v1PointsList = getV1Points(v1Rect, edgeList, firstIntersectEdge, secondIntersectEdge)
        val v2PointsList = getV2Points(v2Rect, edgeList, firstIntersectEdge, secondIntersectEdge)

        if(v1PointsList == null || v2PointsList == null) {
            return null
        }

        val v1RectData = getRectData(v1PointsList)
        val v2RectData = getRectData(v2PointsList)

        val v1Width = if(isLeftAligned) leftViewWidth else rightViewWidth
        val v2Width = if(isLeftAligned) rightViewWidth else leftViewWidth

        Log.d("xxx", "v1 vertical intersection point: $v1PointsList -------- rect data $v1Rect --------- modified rect data $v1RectData")
        Log.d("xxx", "v2 vertical intersection point: $v2PointsList -------- rect data $v2Rect --------- modified rect data $v2RectData")

        val paramsPair = getLayoutParamsPair(leftViewWidth.toInt(), rightViewWidth.toInt())
        val v1Param = if(isLeftAligned) paramsPair.first else paramsPair.second
        val v2Param = if(isLeftAligned) paramsPair.second else paramsPair.first

//        addMarginIfRequired(v1Param, v1RectData, parentRectData)
//        addMarginIfRequired(v2Param, v2RectData, parentRectData)

        return PolygonSplit(
            Split.VERTICAL,
            PolygonData(v1PointsList, v1Width, viewHeight, v1RectData, v1Param),
            PolygonData(v2PointsList, v2Width, viewHeight, v2RectData, v2Param)
        )
    }

    fun addMarginIfRequired(lp : FrameLayout.LayoutParams, rectData: RectData, parentRectData: RectData) {
        Log.d("xxx", "parent rect data: ${parentRectData}, childRectData ${rectData}")
        lp.topMargin = abs(parentRectData.start_y - rectData.start_y).toInt()
        lp.bottomMargin = abs(parentRectData.end_y - rectData.end_y).toInt()
        Log.d("xxx", "margin: top ${lp.topMargin}, bottom ${lp.bottomMargin}")
//        lp.leftMargin = (parentRectData.start_x - rectData.start_x).toInt()
//        lp.rightMargin = (parentRectData.end_x - rectData.end_x).toInt()
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
        min =  min(d_dash.rawX, u_dash.rawX)
        leftViewWidth = abs(max - rectData.start_x)
        rightViewWidth = abs(rectData.end_x - min)
        Log.d("xxx", "parent rect data verti $rectData")
        Log.d("xxx vertical", "v1width = $leftViewWidth ,  v2width = $rightViewWidth")
    }
}