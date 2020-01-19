package com.white_horse.photocollage.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.white_horse.photocollage.R
import java.util.*


class BorderView : View {

    val borderPaint = Paint()
    var borderPath = Path()
    val fillPaint = Paint()
    val rnd = Random()

    init {
        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = resources.getColor(R.color.colorPrimaryDark)
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        fillPaint.color = color
        Log.d("xxx", "border color -- border id ${this.id}, $color")
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    fun setBorderPath(path : Path, borderWidth : Float) {
        borderPath = path
        borderPaint.strokeWidth = borderWidth
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(borderPath, fillPaint)
        canvas?.drawPath(borderPath, borderPaint)
    }
}