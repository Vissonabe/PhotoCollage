package com.white_horse.photocollage.view.polygon

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import android.view.ViewGroup
import com.github.florent37.shapeofview.ShapeOfView
import com.github.florent37.shapeofview.manager.ClipPathManager
import com.white_horse.photocollage.utils.Action
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.PolygonData
import com.white_horse.photocollage.models.PolygonSplit
import com.white_horse.photocollage.models.RectData
import com.white_horse.photocollage.view.BorderView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PolygonView : ShapeOfView {
    private val stateManager =
        StateManager(
            tag?.toString() ?: "",
            this.context
        )
    var action : Action<Pair<PolygonView, PolygonView>>? = null

    private fun addBorderView(path: Path) {
        val borderview = BorderView(context)
        val lp1 =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        borderview.setBorderPath(path, 5f)
        addView(borderview, lp1)
    }

    fun setListener(viewAction : Action<Pair<PolygonView, PolygonView>>?) {
        this.action = viewAction
    }

    fun splitView(down: Point, up: Point) {
        println("xxx split1 ${Thread.currentThread().name}, $tag")
        GlobalScope.launch {
            println("xxx split2 ${Thread.currentThread().name}, $tag")
            val polygonSplit = stateManager.splitView(down, up)
            if(polygonSplit != null) {
                withContext(Dispatchers.Main) {
                    println("xxx split3 ${Thread.currentThread().name}, $tag")

                    val poly1 = addPolygonView1(polygonSplit.v1PolygonData)
                    val poly2 = addPolygonView2(polygonSplit.v2PolygonData)
                    action?.run(Pair(poly1, poly2))
                }
            }
        }
    }

    fun addPolygonView1(polygonData : PolygonData) : PolygonView {
        val polygon1 =
            PolygonView(context)
        polygon1.tag = "polygon_1"
        polygon1.setVertexPoints(
            polygonData.pointList,
            polygonData.width,
            polygonData.height,
            polygonData.rect
        )
        this.addView(polygon1, polygonData.layoutParam)
        return polygon1
    }

    fun addPolygonView2(polygonData : PolygonData): PolygonView {
        val polygon2 =
            PolygonView(context)
        polygon2.tag = "polygon_2"
        polygon2.setVertexPoints(
            polygonData.pointList,
            polygonData.width,
            polygonData.height,
            polygonData.rect
        )
        this.addView(polygon2, polygonData.layoutParam)
        return polygon2
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setVertexPoints(points: List<Point>, width: Float, height: Float, rectData: RectData) {
        println("xxx vertex1 ${Thread.currentThread().name}, $tag, $points")
        GlobalScope.launch {
            println("xxx vertex2 ${Thread.currentThread().name}, $tag")
            stateManager.setVertexPoints(points, width, height, rectData)
            val path = stateManager.getPathFromPoints()
            println("xxx vertex3 ${Thread.currentThread().name}, $tag")
            withContext(Dispatchers.Main) {
                super.setClipPathCreator(object : ClipPathManager.ClipPathCreator {
                    override fun createClipPath(width: Int, height: Int): Path {
                        return path
                    }

                    override fun requiresBitmap(): Boolean {
                        return false
                    }
                })
                addBorderView(path)
            }
        }
    }
}