package com.white_horse.photocollage.view.guideline

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

    var actionMasked: Int = -10
    var action: Action<Pair<Point, Point>>? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val guideLinePaint = Paint()
    private val vertualLinePaint = Paint()

    var downX: Float = 0f
    var downY: Float = 0f
    var upX: Float = 0f
    var upY: Float = 0f
    private val viewManager = GuideLineViewManager(this::onCalculatedVirtualPoints)
    private var virtualStartPoint = Point.getDefaultPoint()
    private var virtualEndPoint = Point.getDefaultPoint()

    init {
        guideLinePaint.strokeWidth = 6f
        guideLinePaint.color = resources.getColor(R.color.colorAccent)
        vertualLinePaint.strokeWidth = 3f
        vertualLinePaint.color = resources.getColor(R.color.black)
    }

    fun onCalculatedVirtualPoints(pair : Pair<Point, Point>) {
        virtualStartPoint = pair.first
        virtualEndPoint = pair.second
        action?.run(pair)
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
                true
            }
            MotionEvent.ACTION_MOVE -> { upX = event.x
                upY = event.y
                invalidate()
                true
            }
            MotionEvent.ACTION_UP -> {
                upX = event.x
                upY = event.y
                viewManager.calculateLineLength(downX, downY, upX, upY)
                invalidate()
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                false
            }
            MotionEvent.ACTION_OUTSIDE -> {
                false
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (actionMasked == MotionEvent.ACTION_MOVE) {
            canvas?.drawLine(downX, downY, upX, upY, guideLinePaint)
        }
//        else if (actionMasked == MotionEvent.ACTION_UP &&
//            viewManager.isLineGreaterThanThreshold(downX, downY, upX, upY)) {
//            canvas?.drawLine(
//                virtualStartPoint.x,
//                virtualStartPoint.y,
//                virtualEndPoint.x,
//                virtualEndPoint.y,
//                vertualLinePaint
//            )
//        }
    }
}