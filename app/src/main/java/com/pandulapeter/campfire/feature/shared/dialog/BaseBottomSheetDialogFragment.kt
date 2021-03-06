package com.pandulapeter.campfire.feature.shared.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatDialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pandulapeter.campfire.R
import com.pandulapeter.campfire.feature.CampfireActivity
import com.pandulapeter.campfire.util.dimension

abstract class BaseBottomSheetDialogFragment<B : ViewDataBinding>(@LayoutRes private val layoutResourceId: Int) : AppCompatDialogFragment() {

    protected lateinit var binding: B
    protected val behavior: BottomSheetBehavior<*> by lazy { ((binding.root.parent as View).layoutParams as CoordinatorLayout.LayoutParams).behavior as BottomSheetBehavior<*> }
    protected val isFullWidth get() = (dialog as CustomWidthBottomSheetDialog).isFullWidth

    open fun initializeDialog(context: Context, savedInstanceState: Bundle?) = Unit

    abstract fun onDialogCreated()

    open fun updateSystemWindows() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
            dialog.window.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR else 0
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = context?.let { context ->
        (activity as? CampfireActivity)?.isUiBlocked = true
        CustomWidthBottomSheetDialog(context, R.style.BottomSheetDialogTheme).apply {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutResourceId, null, false)
            initializeDialog(context, savedInstanceState)
            setContentView(binding.root)
            onDialogCreated()
        }
    } ?: super.onCreateDialog(savedInstanceState)

    override fun onStart() {
        super.onStart()
        updateSystemWindows()
    }

    override fun onCancel(dialog: DialogInterface?) {
        (activity as? CampfireActivity)?.isUiBlocked = false
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        (activity as? CampfireActivity)?.isUiBlocked = false
    }

    private class CustomWidthBottomSheetDialog(context: Context, @StyleRes theme: Int) : BottomSheetDialog(context, theme) {
        private val width = context.dimension(R.dimen.bottom_sheet_width)
        val isFullWidth = width == 0

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (!isFullWidth) {
                window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                window.setGravity(Gravity.BOTTOM)
            }
        }
    }
}