package com.white_horse.photocollage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.models.RectData
import com.white_horse.photocollage.utils.Action
import com.white_horse.photocollage.view.polygon.PolygonView
import java.util.*


class MainActivity : AppCompatActivity() {

    val pointsList = mutableListOf<Point>()
    var x : Float = 0f
    var y = 0f
    val allViewsStack = Stack<PolygonView>()
    val activeViewStack = Stack<PolygonView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        allViewsStack.add(test_polygon)

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
    }

    val viewAction = object :
        Action<Pair<PolygonView, PolygonView>> {
        override fun run(value: Pair<PolygonView, PolygonView>) {
            allViewsStack.add(value.first)
            allViewsStack.add(value.second)
        }
    }

    val action = object :
        Action<Pair<Point, Point>> {
        override fun run(value: Pair<Point, Point>) {
            splitView(value.first, value.second)
        }
    }

    fun splitView(start : Point, end : Point) {
        if(allViewsStack.size == 1) {
            allViewsStack.peek().splitView(start, end)
        } else if(allViewsStack.size > 1) {
            allViewsStack[allViewsStack.size - 1].splitView(start, end)
            allViewsStack[allViewsStack.size - 2].splitView(start, end)
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
