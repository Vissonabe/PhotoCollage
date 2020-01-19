package com.white_horse.photocollage.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.white_horse.photocollage.utils.Action
import com.white_horse.photocollage.R
import com.white_horse.photocollage.models.Point
import com.white_horse.photocollage.utils.getLineLength

class GuidelineView : View {
    val DEBUG_TAG = "xxx"
    val THRESHOLD_LENGTH = 300f

    var actionMasked: Int = -10
    var action: Action<Pair<Point, Point>>? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    val guideLinePaint = Paint()
    val vertualLinePaint = Paint()

    var downX: Float = 0f
    var downY: Float = 0f
    var upX: Float = 0f
    var upY: Float = 0f

    var vertualEndPoint = Point.getDefaultPoint()
    var vertualStartPoint = Point.getDefaultPoint()
    var lineLength = 0f

    init {
        guideLinePaint.strokeWidth = 6f
        guideLinePaint.color = resources.getColor(R.color.colorAccent)
        vertualLinePaint.strokeWidth = 3f
        vertualLinePaint.color = resources.getColor(R.color.black)
    }

    fun setListener(action: Action<Pair<Point, Point>>?) {
        this.action = action
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        actionMasked = event?.actionMasked ?: -10

        return when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
//                Log.d(DEBUG_TAG, "Action was DOWN --- ${event.x} , ${event.y}")
                true
            }
            MotionEvent.ACTION_MOVE -> {
                upX = event.x
                upY = event.y
                invalidate()
//                Log.d(DEBUG_TAG, "Action was MOVE --- ${event.x} , ${event.y}")
                true
            }
            MotionEvent.ACTION_UP -> {
                upX = event.x
                upY = event.y
//                Log.d(DEBUG_TAG, "Action was UP --- ${event.x} , ${event.y}")
                calculateLineLength()
                invalidate()
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun calculateLineLength() {
        lineLength = getLineLength(downX, downY, upX, upY)
        Log.d("xxx", "length of line $lineLength")
        if (isLineGreaterThanThreshold()) {
            findVirtualEndPoint(4)
        }
    }

    private fun findVirtualEndPoint(multiplyFactor: Int = 1) {
        val endX = (upX + ((upX - downX) * multiplyFactor))
        val endY = (upY - ((downY - upY) * multiplyFactor))
        vertualEndPoint = Point.newPoint(endX, endY)

        val startX = (downX - ((upX - downX) * multiplyFactor))
        val startY = (downY + ((downY - upY) * multiplyFactor))

        vertualStartPoint = Point.newPoint(startX, startY)
        action?.run(Pair(vertualStartPoint, vertualEndPoint))
    }

    fun isLineGreaterThanThreshold(): Boolean {
        return lineLength > THRESHOLD_LENGTH
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (actionMasked == MotionEvent.ACTION_MOVE) {
            canvas?.drawLine(downX, downY, upX, upY, guideLinePaint)
        } else if (actionMasked == MotionEvent.ACTION_UP && isLineGreaterThanThreshold()) {
            canvas?.drawLine(
                vertualStartPoint.x,
                vertualStartPoint.y,
                vertualEndPoint.x,
                vertualEndPoint.y,
                vertualLinePaint
            )
        }
    }
}