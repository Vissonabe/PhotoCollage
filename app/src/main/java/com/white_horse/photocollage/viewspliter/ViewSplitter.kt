package com.white_horse.photocollage.viewspliter

import android.widget.FrameLayout
import com.white_horse.photocollage.models.*

abstract class ViewSplitter {
    abstract suspend fun getPolygonSplit(
        p1: Point, p2: Point, parentRectData: RectData, edgeList: List<Edge>,
        firstIntersectEdge: Edge,
        secondIntersectEdge: Edge
    ): PolygonSplit?

    abstract fun getView1Params(
        parentRectData: RectData,
        v1RectData: RectData
    ): FrameLayout.LayoutParams

    abstract fun getView2Params(
        parentRectData: RectData,
        v2RectData: RectData
    ): FrameLayout.LayoutParams

    private fun getV1Points(
        edgeList: List<Edge>,
        firstIntersectEdge: Edge,
        secondIntersectEdge: Edge
    ): List<Point>? {
        val list = mutableListOf<Point>()
        var index = 0

        if (edgeList.isNotEmpty()) {
            while (index < edgeList.size) {
                val edge = edgeList[index]
                if (edge.hasIntersection) {
                    list.add(Point.newPoint(edge.start.rawX, edge.start.rawY))
                    list.add(
                        Point.newPoint(
                            edge.intersectionPoint.rawX,
                            edge.intersectionPoint.rawY
                        )
                    )
                    list.add(
                        Point.newPoint(
                            secondIntersectEdge.intersectionPoint.rawX,
                            secondIntersectEdge.intersectionPoint.rawY
                        )
                    )
                    index = secondIntersectEdge.index + 1
                } else {
                    list.add(Point.newPoint(edge.start.rawX, edge.start.rawY))
                    index++
                }
            }
            return list
        } else {
            return null
        }
    }

    private fun getV2Points(
        edgeList: List<Edge>,
        firstIntersectEdge: Edge,
        secondIntersectEdge: Edge
    ): List<Point>? {
        val v2PointsList = mutableListOf<Point>()
        var index = firstIntersectEdge.index

        if (edgeList.isNotEmpty() && index >= 0) {
            while (index < edgeList.size) {
                val edge = edgeList[index]
                if (edge.hasIntersection) {
                    if (edge.index == secondIntersectEdge.index) {
                        v2PointsList.add(
                            Point.newPoint(
                                edge.intersectionPoint.rawX,
                                edge.intersectionPoint.rawY
                            )
                        )
                        break
                    } else {
                        v2PointsList.add(
                            Point.newPoint(
                                edge.intersectionPoint.rawX,
                                edge.intersectionPoint.rawY
                            )
                        )
                        v2PointsList.add(Point.newPoint(edge.end.rawX, edge.end.rawY))
                    }
                } else {
                    v2PointsList.add(Point.newPoint(edge.end.rawX, edge.end.rawY))
                }
                index++
            }

            return v2PointsList
        } else {
            return null
        }
    }

    fun modifyPoints(rectData: RectData, points: List<Point>): List<Point> {
        val list = mutableListOf<Point>()
        points.forEachIndexed { index, point ->
            list.add(
                Point(
                    point.rawX - rectData.start_x,
                    point.rawY - rectData.start_y,
                    point.rawX,
                    point.rawY
                )
            )
        }
        return list
    }

    suspend fun getPolygonSplitInternal(
        p1: Point,
        p2: Point,
        parentRectData: RectData,
        edgeList: List<Edge>,
        firstIntersectEdge: Edge,
        secondIntersectEdge: Edge
    ): PolygonSplit? {
        val v1PointsList = getV1Points(edgeList, firstIntersectEdge, secondIntersectEdge)
        val v2PointsList = getV2Points(edgeList, firstIntersectEdge, secondIntersectEdge)

        if (v1PointsList == null || v2PointsList == null) {
            return null
        }

        val v1RectData = getRectData(v1PointsList)
        val v2RectData = getRectData(v2PointsList)

        val modifiedV1Points = modifyPoints(v1RectData, v1PointsList)
        val modifiedV2Points = modifyPoints(v2RectData, v2PointsList)

        val v1Width = v1RectData.end_x - v1RectData.start_x
        val v2Width = v2RectData.end_x - v2RectData.start_x

        val v1Height = v1RectData.end_y - v1RectData.start_y
        val v2Height = v2RectData.end_y - v2RectData.start_y

        val v1Param = getView1Params(parentRectData, v1RectData)
        val v2Param = getView1Params(parentRectData, v2RectData)

        return PolygonSplit(
            Split.VERTICAL,
            PolygonData(modifiedV1Points, v1Width, v1Height, v1RectData, v1Param),
            PolygonData(modifiedV2Points, v2Width, v2Height, v2RectData, v2Param)
        )
    }
}