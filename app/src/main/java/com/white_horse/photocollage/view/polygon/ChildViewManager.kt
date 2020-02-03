package com.white_horse.photocollage.view.polygon

import android.content.Context
import android.graphics.Path
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import com.white_horse.photocollage.R
import com.white_horse.photocollage.models.ChildPolygonsData
import com.white_horse.photocollage.models.PolygonData
import com.white_horse.photocollage.utils.Action
import com.white_horse.photocollage.utils.LogTrace
import com.white_horse.photocollage.utils.Polygon
import com.white_horse.photocollage.utils.concatString
import com.white_horse.photocollage.view.BorderView
import com.white_horse.photocollage.view.TouchImageView

class ChildViewManager(val view : PolygonView) {

    val context : Context = view.context
    val VIEW_ONE_ID = "1"
    val VIEW_TWO_ID = "2"

    fun addBorderView(path: Path) {
        val borderView = BorderView(context)
        val lp1 =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        borderView.setBorderPath(path, 5f)
        view.addView(borderView, lp1)
    }

    fun addTouchImageView(path: Path, polygon: Polygon) {
        val image = TouchImageView(context)
        image.setPathAndPolygon(path, polygon)
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        val lp1 = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        image.layoutParams = lp1
        image.setImageResource(R.drawable.adventure)
//      Glide.with(context).load(R.drawable.adventure).centerCrop().into(image)
        view.addView(image, lp1)
    }

    fun hideTouchImageView() {
        var imageViewIndex = -1
        view.children.forEachIndexed { index, view ->
            if(view is TouchImageView) {
                imageViewIndex = index
            }
        }
        if(imageViewIndex > -1) {
            //todo fix this -->
            view.getChildAt(imageViewIndex).visibility = View.GONE
        }
    }

    fun addGetPolygonView1(polygonData : PolygonData, viewAction : Action<ChildPolygonsData>?) : PolygonView {
        val polygon1 = PolygonView(context)
        val uniqueId = concatString(view.getUniqueId(),  VIEW_ONE_ID)
        polygon1.setUniqueId(uniqueId)
        polygon1.setListener(viewAction)
        polygon1.tag = "polygon_$uniqueId"
        polygon1.setVertexPoints(
            polygonData.pointList,
            polygonData.width,
            polygonData.height,
            polygonData.rect
        )
        view.addView(polygon1,0, polygonData.layoutParam)
        return polygon1
    }

    fun addGetPolygonView2(polygonData : PolygonData, viewAction : Action<ChildPolygonsData>?): PolygonView {
        val polygon2 = PolygonView(context)
        val uniqueId = concatString(view.getUniqueId(),  VIEW_TWO_ID)
        polygon2.tag = "polygon_$uniqueId"
        polygon2.setListener(viewAction)
        polygon2.setUniqueId(uniqueId)
        polygon2.setVertexPoints(
            polygonData.pointList,
            polygonData.width,
            polygonData.height,
            polygonData.rect
        )
        view.addView(polygon2, 1, polygonData.layoutParam)
        return polygon2
    }
}