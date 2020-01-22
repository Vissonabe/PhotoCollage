package com.white_horse.photocollage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.white_horse.photocollage.models.ChildPolygonsData
import kotlinx.android.synthetic.main.activity_main.*
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.RectData
import com.white_horse.photocollage.models.ViewTree
import com.white_horse.photocollage.utils.Action
import com.white_horse.photocollage.utils.addChildren
import com.white_horse.photocollage.utils.getActivePolygonsList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {

    val pointsList = mutableListOf<Point>()
    var x : Float = 0f
    var y = 0f
    val undoViewStack = Stack<String>()
    lateinit var rootViewTree : ViewTree
    var isOnEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        x = resources.displayMetrics.widthPixels.toFloat()
        y = resources.displayMetrics.heightPixels.toFloat()

        println("xxx -- $x --- $y")
        pointsList.add(Point(0f, 0f, 0f, 0f))
        pointsList.add(Point(0f, y, 0f, y))
        pointsList.add(Point(x, y, x, y))
        pointsList.add(Point(x, 0f, x, 0f))

        test_polygon.setVertexPoints(pointsList, x , y,
            RectData(0f, 1080f, 0f, 1920f)
        )
        test_polygon.setUniqueId("0")
        rootViewTree = ViewTree(test_polygon)

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
