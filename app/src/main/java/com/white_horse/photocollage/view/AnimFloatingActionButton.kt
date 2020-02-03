package com.white_horse.photocollage.view

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.white_horse.photocollage.R

class AnimFloatingActionButton : FloatingActionButton{

    private var avdSecondToFirst: AnimatedVectorDrawableCompat? = null
    private var avdFirstToSecond: AnimatedVectorDrawableCompat? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        attrs?.let {
            init(context, attrs)
        }
    }

    private fun init(
        context: Context,
        attrs: AttributeSet
    ) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AnimFloatingActionButton,
            0, 0
        )
        @DrawableRes val avdFromRes: Int
        @DrawableRes val avdToRes: Int
        try {
            avdFromRes = a.getResourceId(R.styleable.AnimFloatingActionButton_avdFirst, -1)
            avdToRes = a.getResourceId(R.styleable.AnimFloatingActionButton_avdSecond, -1)
        } finally {
            a.recycle()
        }

        avdFirstToSecond = AnimatedVectorDrawableCompat.create(getContext(), avdFromRes)
        avdSecondToFirst = AnimatedVectorDrawableCompat.create(getContext(), avdToRes)
        if (avdSecondToFirst == null || avdFirstToSecond == null) {
            throw RuntimeException("Drawable is not a valid AnimatedVectorDrawable")
        } else {
            setImageDrawable(avdFirstToSecond)
        }
    }

    fun animateAvdFirst() {
        morph(true)
    }

    fun animateAvdSecond() {
        morph(false)
    }

    fun morph(animateFirst : Boolean) {
        val drawable: AnimatedVectorDrawableCompat? =
            if (animateFirst) avdFirstToSecond else avdSecondToFirst
        setImageDrawable(drawable)
        drawable?.start()
    }

    fun setAvdFirst(avdFirst: AnimatedVectorDrawableCompat) {
        avdFirstToSecond = avdFirst
        invalidate()
    }

    fun setAvdSecond(avdSecond: AnimatedVectorDrawableCompat) {
        avdSecondToFirst = avdSecond
        invalidate()
    }

}