package com.white_horse.photocollage.view.polygon

import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import com.github.florent37.shapeofview.ShapeOfView
import com.github.florent37.shapeofview.manager.ClipPathManager
import com.white_horse.photocollage.models.ChildPolygonsData
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.RectData
import com.white_horse.photocollage.utils.Action
import com.white_horse.photocollage.utils.LogTrace
import com.white_horse.photocollage.utils.Polygon
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
    private var action : Action<ChildPolygonsData>? = null
    var viewUniqueId : String = "-1"
    private val childViewFactory = ChildViewManager(this)

    fun setListener(viewAction : Action<ChildPolygonsData>?) {
        this.action = viewAction
    }

    fun setUniqueId(uniqueId : String) {
        this.viewUniqueId = uniqueId
    }

    fun getUniqueId() : String {
        return viewUniqueId
    }

    fun splitView(down: Point, up: Point) {
        GlobalScope.launch {
            val polygonSplit = stateManager.splitView(down, up)
            if(polygonSplit != null) {
                withContext(Dispatchers.Main) {
                    childViewFactory.hideTouchImageView()
                    val poly1 = childViewFactory.addGetPolygonView1(polygonSplit.v1PolygonData, action)
                    val poly2 = childViewFactory.addGetPolygonView2(polygonSplit.v2PolygonData, action)
                    action?.run(ChildPolygonsData(viewUniqueId, poly1, poly2))
                    LogTrace.e("view split done")
                }
            }
        }
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setVertexPoints(points: List<Point>, width: Float, height: Float, rectData: RectData) {
        GlobalScope.launch {
            val polygonView = Polygon(points)
            stateManager.setVertexPoints(points, width, height, rectData)
            val path = stateManager.getPathFromPoints()
            withContext(Dispatchers.Main) {
                super.setClipPathCreator(object : ClipPathManager.ClipPathCreator {
                    override fun createClipPath(width: Int, height: Int): Path {
                        return path
                    }

                    override fun requiresBitmap(): Boolean {
                        return true
                    }
                })
                LogTrace.d("rootpolygon area ${polygonView.area()}")
                childViewFactory.addTouchImageView(path, polygonView)
                childViewFactory.addBorderView(path)
            }
        }
    }
}