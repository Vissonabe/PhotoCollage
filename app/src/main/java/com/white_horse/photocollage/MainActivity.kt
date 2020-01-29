package com.white_horse.photocollage

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.white_horse.photocollage.models.ChildPolygonsData
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.RectData
import com.white_horse.photocollage.models.ViewTree
import com.white_horse.photocollage.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {

    val undoViewStack = Stack<String>()
    lateinit var rootViewTree : ViewTree
    var isOnEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRootPolygon()
        fab.setOnClickListener {
            if(!isOnEditMode) {
                isOnEditMode = true
                it.setBackgroundResource(R.drawable.ic_close)
                guideline_view.visibility = View.GONE
            } else {
                isOnEditMode = false
                it.setBackgroundResource(R.drawable.ic_edit)
                guideline_view.visibility = View.VISIBLE
            }
        }
    }

    private fun initRootPolygon() {
        val x = resources.displayMetrics.widthPixels.toFloat()
        val y = resources.displayMetrics.heightPixels.toFloat()
        val points = getPointsList(0f ,0f ,x, y)
        test_polygon.setVertexPoints(points, x , y,
            RectData(0f, x, 0f, y)
        )
        test_polygon.setUniqueId("0")
        rootViewTree = ViewTree(test_polygon)
    }

    val viewAction = object :
        Action<ChildPolygonsData> {
        override fun run(value: ChildPolygonsData) {
            GlobalScope.launch {
                undoViewStack.add(value.parentPolygonId)
                addChildren(rootViewTree, value)
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
            getActivePolygonsList(rootViewTree).forEach {
                it.splitView(start, end)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        test_polygon.setListener(viewAction)
        guideline_view.setListener(action)
    }

    override fun onStop() {
        super.onStop()
        test_polygon.setListener(null)
        guideline_view.setListener(null)
    }
}
