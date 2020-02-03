package com.white_horse.photocollage.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.white_horse.photocollage.R
import com.white_horse.photocollage.models.ChildPolygonsData
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.RectData
import com.white_horse.photocollage.models.ViewTree
import com.white_horse.photocollage.utils.Action
import com.white_horse.photocollage.utils.getPointsList
import com.white_horse.photocollage.view.AnimFloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    var isOnEditMode = false
    val viewTreeManager = ViewTreeManager(this::handleUndoFABVisibility)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRootPolygon()
        edit_fab.setOnClickListener {
            if(!isOnEditMode) {
                isOnEditMode = true
                (it as AnimFloatingActionButton).animateAvdFirst()
                guideline_view.visibility = View.GONE
            } else {
                isOnEditMode = false
                (it as AnimFloatingActionButton).animateAvdSecond()
                guideline_view.visibility = View.VISIBLE
            }
        }
        undo_fab.setOnClickListener {
            GlobalScope.launch {
                viewTreeManager.clearActivePolygonsUndoAction()
            }
        }
    }

    private fun handleUndoFABVisibility(show : Boolean) {
        runOnUiThread {
            if (show && !undo_fab.isVisible) {
                undo_fab.show()
            } else if (!show && undo_fab.isVisible) {
                undo_fab.hide()
            }
        }
    }

    private fun initRootPolygon() {
        val x = resources.displayMetrics.widthPixels.toFloat()
        val y = resources.displayMetrics.heightPixels.toFloat()
        val points = getPointsList(0f ,0f ,x, y)
        root_polygon.setVertexPoints(points, x , y,
            RectData(0f, x, 0f, y)
        )
        root_polygon.setUniqueId("0")
        root_polygon.setListener(viewAction)
        val root = ViewTree(root_polygon)
        viewTreeManager.initViewTree(root)
    }

    val viewAction = object :
        Action<ChildPolygonsData> {
        override fun run(value: ChildPolygonsData) {
            GlobalScope.launch {
                viewTreeManager.addChildren(value)
            }
        }
    }

    val action = object :
        Action<Pair<Point, Point>> {
        override fun run(value: Pair<Point, Point>) {
            splitView(value.first, value.second)
        }
    }

    fun splitView(start : Point, end : Point) {
        GlobalScope.launch {
            viewTreeManager.splitActivePolygonsList(start, end)
        }
    }

    override fun onStart() {
        super.onStart()
        guideline_view.setListener(action)
    }

    override fun onStop() {
        super.onStop()
        guideline_view.setListener(null)
    }
}
