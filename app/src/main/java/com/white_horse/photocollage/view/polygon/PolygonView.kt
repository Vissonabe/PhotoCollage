package com.white_horse.photocollage.view.polygon

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Path
import android.media.Image
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.florent37.shapeofview.ShapeOfView
import com.github.florent37.shapeofview.manager.ClipPathManager
import com.white_horse.photocollage.R
import com.white_horse.photocollage.models.*
import com.white_horse.photocollage.utils.Action
import com.white_horse.photocollage.utils.concatenateInt
import com.white_horse.photocollage.view.BorderView
import com.white_horse.photocollage.view.TouchImageView
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
    var action : Action<ChildPolygonsData>? = null
    var viewUniqueId : Int = -1

    private fun addBorderView(path: Path) {
        val borderview = BorderView(context)
        val lp1 =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        borderview.setBorderPath(path, 5f)
        addView(borderview, lp1)
    }

    private fun addImageView() {
        val image = TouchImageView(context)
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        val lp1 = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        image.layoutParams = lp1
        image.setImageResource(R.drawable.unsplash)
        addView(image, lp1)

//        Glide.with(image)
//            .load(BitmapFactory.decodeResource(resources, R.drawable.unsplash))
//            .centerCrop()
//            .into(image)
    }

    fun setListener(viewAction : Action<ChildPolygonsData>?) {
        this.action = viewAction
    }

    fun setUniqueId(uniqueId : Int) {
        this.viewUniqueId = uniqueId
    }

    fun getUniqueId() : Int {
        return viewUniqueId
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
                    action?.run(ChildPolygonsData(viewUniqueId, poly1, poly2))
                }
            }
        }
    }

    fun addPolygonView1(polygonData : PolygonData) : PolygonView {
        val polygon1 =
            PolygonView(context)
        polygon1.setUniqueId(concatenateInt(viewUniqueId, viewUniqueId + 1))
        polygon1.setListener(action)
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
        polygon2.setListener(action)
        polygon2.setUniqueId(concatenateInt(viewUniqueId, viewUniqueId + 2))
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
                addImageView()
                addBorderView(path)
            }
        }
    }
}