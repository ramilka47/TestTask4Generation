package com.test.task.app.ui.dialogs.helpfull

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

internal class DialogRootView : ViewGroup, DialogRoot {
    private var dialog: DialogController? = null
    private var content: View? = null
    private var cancelledOnTouchOutside = false
    private var dialogWidth = 0

    private var backgroundDimAmount: Float = 0f

    override var dialogContent: View?
        get() = gDialogContent()
        set(value) {
            this.dialogContent = value
        }

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
        val dp = context.resources.displayMetrics.density

        var width = ((context as ContextThemeWrapper).baseContext as Activity).window.attributes.width
        width -= (50 * 2 * dp).toInt()

        dialogWidth = width
        backgroundDimAmount = ResourcesUtils.getAttrFloat(context, R.attr.backgroundDimAmount)
        // Ensure backgroundDimAmount is in range
        backgroundDimAmount = clamp(backgroundDimAmount, 0.0f, 1.0f)
        val alpha = (255 * backgroundDimAmount).toInt()
        setBackgroundColor(Color.argb(alpha, 0, 0, 0))
    }

    fun setDialog(dialog: DialogController?) {
        this.dialog = dialog
    }

    fun setCancelledOnTouchOutside(cancel: Boolean) {
        cancelledOnTouchOutside = cancel
    }

    private fun ensureContent() {
        if (content == null) {
            check(childCount != 0) { "DialogRoot should contain a DialogContent" }
            content = getChildAt(0)
        }
    }

    private fun isUnderView(view: View?, event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        return x >= view!!.left && x < view.right && y >= view.top && y < view.bottom
    }

    private fun cancel() {
        if (dialog != null) {
            dialog!!.cancel()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (cancelledOnTouchOutside && event.actionMasked == MotionEvent.ACTION_DOWN) {
            ensureContent()
            if (!isUnderView(content, event)) {
                cancel()
            }
        }
        // Always return true to avoid touch through
        return true
    }

    // android.view.ViewRootImpl
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        ensureContent()
        val content = content
        var baseWidth = dialogWidth
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val lp = content!!.layoutParams
        val resizeWidth: Boolean
        var childWidthMeasureSpec: Int
        if (lp.width == LayoutParams.WRAP_CONTENT &&
            (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) && widthSize > baseWidth
        ) {
            resizeWidth = true
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(baseWidth, MeasureSpec.AT_MOST)
        } else {
            resizeWidth = false
            childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, lp.width)
        }
        val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, lp.height)
        content.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        if (resizeWidth && content.measuredWidthAndState and MEASURED_STATE_TOO_SMALL != 0) {
            // Didn't fit in that width... try expanding a bit.
            baseWidth = (baseWidth + widthSize) / 2
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(baseWidth, MeasureSpec.AT_MOST)
            content.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
        if (resizeWidth && content.measuredWidthAndState and MEASURED_STATE_TOO_SMALL != 0) {
            // Still didn't fit in that width... restore.
            childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, lp.width)
            content.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
        val width = getMeasuredDimension(widthMeasureSpec, content.measuredWidth)
        val height = getMeasuredDimension(heightMeasureSpec, content.measuredHeight)
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        ensureContent()
        val content = content
        val contentWidth = content!!.measuredWidth
        val contentHeight = content.measuredHeight
        val left = (width - contentWidth) / 2
        val top = (height - contentHeight) / 2
        content.layout(left, top, left + contentWidth, top + contentHeight)
    }

    private fun gDialogContent(): View {
        ensureContent()
        return content!!
    }

    companion object {
        fun clamp(x: Float, bound1: Float, bound2: Float): Float {
            if (bound2 >= bound1) {
                if (x > bound2) return bound2
                if (x < bound1) return bound1
            } else {
                if (x > bound1) return bound1
                if (x < bound2) return bound2
            }
            return x
        }

        private fun getMeasuredDimension(spec: Int, childDimension: Int): Int {
            val size = MeasureSpec.getSize(spec)
            val mode = MeasureSpec.getMode(spec)
            return if (mode == MeasureSpec.EXACTLY || mode == MeasureSpec.AT_MOST) {
                size
            } else {
                childDimension
            }
        }
    }
}
