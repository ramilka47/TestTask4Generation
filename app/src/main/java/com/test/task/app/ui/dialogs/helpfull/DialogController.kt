package com.test.task.app.ui.dialogs.helpfull

import android.R
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.task.R.*
import com.test.task.app.ui.controllers.MvpController

/**
 * `DialogController` shows a view in dialog style.
 */
abstract class DialogController : MvpController, DialogInterface {
    /**
     * Returns the theme ID passed in constructor.
     * `0` if using the default dialog theme.
     */
    var themeId: Int
        private set
    private var actualThemeId = 0
    private var cancellable = true
    private var cancelledOnTouchOutside = true
    private var cancelled = false
    private var dismissed = false
    /**
     * Creates a dialog controller that uses a custom dialog style.
     */
    /**
     * Creates a dialog controller that uses the default dialog theme.
     */
    @JvmOverloads
    constructor(themeId: Int = 0) : super() {

        // Put args
        val args = args
        args.putInt(KEY_THEME_ID, themeId)
        this.themeId = themeId
    }

    /**
     * Returns the actual theme ID.
     * It resolve the default theme.
     */
    fun getActualThemeId(): Int {
        return if (themeId == 0) {
            val outValue = TypedValue()
            activity!!.theme.resolveAttribute(R.attr.dialogTheme, outValue, true)
            outValue.resourceId
        } else {
            themeId
        }
    }

    // Applies theme to LayoutInflater
    private fun resolveLayoutInflater(inflater: LayoutInflater): LayoutInflater {
        var inflater = inflater
        if (actualThemeId == 0) {
            actualThemeId = getActualThemeId()
        }
        if (actualThemeId != 0) {
            val contextThemeWrapper: Context = ContextThemeWrapper(inflater.context, actualThemeId)
            inflater = inflater.cloneInContext(contextThemeWrapper)
        }
        return inflater
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        var inflater = inflater
        inflater = resolveLayoutInflater(inflater)
        val view: View = inflater.inflate(layout.cd_controller_dialog, container, false)
        val root = view.findViewById<View>(id.cd_dialog_root) as DialogRootView
        root.setDialog(this)
        root.setCancelledOnTouchOutside(cancelledOnTouchOutside)
        val content = view.findViewById<View>(id.cd_dialog_content) as ViewGroup
        val dialogContent = onCreateContentView(inflater, content)
        if (dialogContent != null) {
            content.addView(dialogContent)
        }
        return view
    }

    /**
     * Called when the dialog is ready to display its view. `null` could be returned.
     * The standard body for this method will be
     * `return inflater.inflate(R.layout.my_layout, container, false);`, plus any binding code.
     *
     * @param inflater The LayoutInflater that should be used to inflate views
     * @param container The parent view that this dialog's view will eventually be attached to.
     * This dialog's view should NOT be added in this method. It is simply passed in
     * so that valid LayoutParams can be used during inflation.
     */
    abstract fun onCreateContentView(
        inflater: LayoutInflater?, container: ViewGroup?
    ): View?

    override fun onDestroy() {
        super.onDestroy()
        // Avoid missing onDismiss()
        if (!dismissed) {
            dismissed = true
            onDismiss()
        }
    }

    /**
     * Sets whether this dialog is cancellable with the
     * [BACK][android.view.KeyEvent.KEYCODE_BACK] key.
     */
    fun setCancellable(flag: Boolean) {
        cancellable = flag
    }

    /**
     * Sets whether this dialog is cancelled when touched outside the window's
     * bounds. If setting to true, the dialog is set to be cancellable if not
     * already set.
     *
     * @param cancel Whether the dialog should be cancelled when touched outside
     * the window.
     */
    fun setCancelledOnTouchOutside(cancel: Boolean) {
        if (cancel && !cancellable) {
            cancellable = true
        }
        if (cancelledOnTouchOutside != cancel) {
            cancelledOnTouchOutside = cancel
            val view = view
            if (view != null) {
                val root = view.findViewById<View>(id.cd_dialog_root) as DialogRootView
                root?.setCancelledOnTouchOutside(cancel)
            }
        }
    }

    /**
     * Cancel the dialog. This is essentially the same as calling [.dismiss], but it will
     * also call [.onCancel].
     */
    override fun cancel() {
        if (!cancelled && !dismissed) {
            cancelled = true
            onCancel()
            dismiss()
        }
    }

    /**
     * Dismiss this dialog, removing it from the screen. it will
     * also call [.onDismiss].
     */
    override fun dismiss() {
        if (!dismissed) {
            dismissed = true
            onDismiss()
            val router = router
            if (router != null) {
                if (!router.popController(this)) {
                    // Handle activity finishing
                    val activity = activity
                    activity?.finish()
                }
            }
        }
    }

    /**
     * This method will be invoked when the dialog is cancelled.
     */
    protected fun onCancel() {}

    /**
     * This method will be invoked when the dialog is dismissed.
     */
    protected fun onDismiss() {}

    override fun handleBack(): Boolean {
        var result = super.handleBack()
        result = !cancellable || result
        if (!result) {
            // This dialog will be cancelled
            if (!cancelled && !dismissed) {
                cancelled = true
                onCancel()
            }
        }
        return result
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setCancellable(savedInstanceState.getBoolean(KEY_CANCELLABLE, cancellable))
        setCancelledOnTouchOutside(
            savedInstanceState.getBoolean(
                KEY_CANCELLED_ON_TOUCH_OUTSIDE, cancelledOnTouchOutside
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_CANCELLABLE, cancellable)
        outState.putBoolean(KEY_CANCELLED_ON_TOUCH_OUTSIDE, cancelledOnTouchOutside)
    }

    companion object {
        private const val KEY_THEME_ID = "DialogController:theme_id"
        private const val KEY_CANCELLABLE = "DialogController:cancellable"
        private const val KEY_CANCELLED_ON_TOUCH_OUTSIDE =
            "DialogController:cancelled_on_touch_outside"
    }
}
