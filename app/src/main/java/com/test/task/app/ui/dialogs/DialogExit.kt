package com.test.task.app.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.test.task.R
import com.test.task.app.Application
import com.test.task.app.mvp.presenters.PresenterExit
import com.test.task.app.mvp.views.IExitView
import com.test.task.app.ui.fragments.SearchFragment
import kotlinx.android.synthetic.main.dialog_exit.*
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class DialogExit : MvpAppCompatDialogFragment(), IExitView {

    @Inject
    @InjectPresenter
    lateinit var presenter : PresenterExit

    @ProvidePresenter
    fun provides() = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireContext().applicationContext as Application).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_exit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_one.setText(R.string.you_sure_for_exit)
        text_two.setText(R.string.all_info_you_profile_will_be_to_lose)

        button1.setText(R.string.cancel)
        button1.setOnClickListener {
            dismiss()
        }

        button2.setText(R.string.logout)
        button2.setOnClickListener {
            presenter.logout()
        }
    }

    override fun onExitError() {
        targetFragment?.onActivityResult(targetRequestCode, SearchFragment.RESULT_CODE_ERROR, null)
        dismiss()
    }

    override fun onExitSuccess() {
        targetFragment?.onActivityResult(targetRequestCode, SearchFragment.RESULT_CODE, null)
        dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context).show()
        builder.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_rounder_8_white))
        builder.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        builder.window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        builder.window?.setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return builder
    }

}