package com.test.task.app.ui.dialogs.helpfull

import android.R
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.core.view.ViewCompat

internal class DialogContentView : FrameLayout {
    private val minWidthMajor = TypedValue()
    private val minWidthMinor = TypedValue()

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?, defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val theme = context.theme
        theme.resolveAttribute(R.attr.windowMinWidthMajor, minWidthMajor, true)
        theme.resolveAttribute(R.attr.windowMinWidthMinor, minWidthMinor, true)
        val drawable = ResourcesUtils.getAttrDrawable(context, R.attr.windowBackground)
        ViewCompat.setBackground(this, drawable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val elevation = ResourcesUtils.getAttrDimension(context, R.attr.windowElevation)
            ViewCompat.setElevation(this, elevation)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val tv = if (isPortrait) minWidthMinor else minWidthMajor
        var measure = false
        if (widthMode == MeasureSpec.AT_MOST && tv.type != TypedValue.TYPE_NULL) {
            val min: Int
            val metrics = context.resources.displayMetrics
            min = if (tv.type == TypedValue.TYPE_DIMENSION) {
                tv.getDimension(metrics).toInt()
            } else if (tv.type == TypedValue.TYPE_FRACTION) {
                tv.getFraction(metrics.widthPixels.toFloat(), metrics.widthPixels.toFloat()).toInt()
            } else {
                0
            }
            if (measuredWidth < min) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(min, MeasureSpec.EXACTLY)
                measure = true
            }
        }
        if (measure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
