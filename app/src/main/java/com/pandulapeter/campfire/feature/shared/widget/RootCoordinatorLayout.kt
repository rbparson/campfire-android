package com.pandulapeter.campfire.feature.shared.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.WindowInsets
import com.pandulapeter.campfire.util.obtainColor

class RootCoordinatorLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private val paint = Paint().apply { color = context.obtainColor(android.R.attr.colorPrimary) }
    var insetChangeListener: (statusBarHeight: Int) -> Unit = {}
        set(value) {
            field = value
            value(statusBarHeight)
        }
    private var statusBarHeight = 0

    init {
        fitsSystemWindows = true
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        insets?.systemWindowInsetTop?.let {
            if (statusBarHeight != it) {
                statusBarHeight = it
                insetChangeListener(it)
            }
        }
        return super.dispatchApplyWindowInsets(insets)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.run {
            drawRect(0f, 0f, width.toFloat(), statusBarHeight.toFloat(), paint)
            super.dispatchDraw(this)
        }
    }
}