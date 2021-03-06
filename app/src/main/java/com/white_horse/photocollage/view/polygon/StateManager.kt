package com.white_horse.photocollage.view.polygon

import android.content.Context
import android.graphics.Path
import android.util.Log
import android.widget.FrameLayout
import com.white_horse.photocollage.models.*
import com.white_horse.photocollage.utils.*
import com.white_horse.photocollage.viewspliter.*

class StateManager(val context: Context) {
    private val edgeList = mutableListOf<Edge>()
    var rectData: RectData = RectData.getDefaultRect()
    var viewTag: String = ""

    private val pointsList = mutableListOf<Point>()
    var viewWidth: Float = 0f
    var viewHeight: Float = 0f
    var d_dash = Point.getDefaultPoint()
    var u_dash = Point.getDefaultPoint()
    var firstIntersectEdge = Edge.getDefaultEdge()
    var secondIntersectEdge = Edge.getDefaultEdge()
    private val verticalSplitter: ViewSplitter
    private val horizontalSplitter: ViewSplitter
    val diagonalSplitter = DiagonalSplitter(this::getViewTagAction, context)

    init {
        verticalSplitter = VerticalSplitter(this::getViewTagAction, context)
        horizontalSplitter = HorizontalSplitter(this::getViewTagAction, context)
    }

    fun getViewTagAction(): String {
        return viewTag
    }

    fun setVertexPoints(points: List<Point>, width: Float, height: Float, rectData: RectData) {
        this.rectData = rectData
        pointsList.clearAndAdd(points)
        viewHeight = height
        viewWidth = width
        createEdgesFromPoints(points)
    }

    private fun createEdgesFromPoints(points: List<Point>) {
        edgeList.clear()
        for (i in points.indices) {
            var endIndex = i + 1
            endIndex = if (endIndex == points.size) 0 else endIndex
            edgeList.add(
                Edge(
                    i,
                    pointsList[i],
                    pointsList[endIndex]
                )
            )
        }
    }

    fun getPathFromPoints(): Path {
        val path = Path()
        if (pointsList.isNotEmpty()) {
            path.moveTo(pointsList[0])
            for (i in 1 until pointsList.size) {
                path.lineTo(pointsList[i])
            }
            path.lineTo(pointsList[0])
            path.close()
        }
        return path
    }

    private suspend fun findIntersectionInEdge(down: Point, up: Point) {
        d_dash = down
        u_dash = up

        for (edge in edgeList) {
            val p1 = Point(
                edge.start.rawX,
                edge.start.rawY
            )
            val p2 = Point(
                edge.end.rawX,
                edge.end.rawY
            )
            val p3 =
                Point(down.rawX, down.rawY)
            val p4 = Point(up.rawX, up.rawY)
            val intersection =
                findIntersection(
                    p1,
                    p2,
                    p3,
                    p4
                )
            if (intersection != null) {
                edge.hasIntersection = true
                edge.intersectionPoint = intersection
//                Log.d("xxx", "intersection point $intersection")

                if (Edge.isDefaultEdge(firstIntersectEdge)) {
                    firstIntersectEdge = edge
                    d_dash = edge.intersectionPoint
                } else {
                    secondIntersectEdge = edge
                    u_dash = edge.intersectionPoint
                }
            }
        }

        Log.d("xxx", "d_dash =  $d_dash, u_dash = $u_dash")
    }

    suspend fun splitView(down: Point, up: Point): PolygonSplit? {
        val split = getSplitDirection(context, down, up)

        LogTrace.d("split direction $split")

        findIntersectionInEdge(down, up)

        if(firstIntersectEdge.index == -1 || secondIntersectEdge.index == -1 || edgeList.isEmpty()) {
            return null
        }

        return when (split) {
            Split.HORIZONTAL -> {
                horizontalSplitter.getPolygonSplit(
                    d_dash,
                    u_dash,
                    rectData,
                    edgeList.toList(),
                    firstIntersectEdge,
                    secondIntersectEdge
                )
            }
            Split.VERTICAL -> {
                verticalSplitter.getPolygonSplit(
                    d_dash,
                    u_dash,
                    rectData,
                    edgeList.toList(),
                    firstIntersectEdge,
                    secondIntersectEdge
                )
            }
            Split.DIAGONAL -> {
                diagonalSplitter.getPolygonSplit(
                    d_dash,
                    u_dash,
                    rectData,
                    edgeList.toList(),
                    firstIntersectEdge,
                    secondIntersectEdge
                )
            }
            Split.UNKNOWN -> null
        }
    }
}