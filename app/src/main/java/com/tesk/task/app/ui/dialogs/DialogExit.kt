package com.tesk.task.app.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.tesk.task.R
import com.tesk.task.app.mvp.presenters.PresenterExit
import com.tesk.task.app.mvp.views.IExitView
import com.tesk.task.app.ui.fragments.SearchFragment
import kotlinx.android.synthetic.main.dialog_exit.*
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class DialogExit : DialogFragment(), IExitView {

    @InjectPresenter
    lateinit var presenter : PresenterExit

    private var mMvpDelegate = MvpDelegate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mMvpDelegate.onCreate(savedInstanceState)
    }

    override fun onDetach() {
        super.onDetach()
        mMvpDelegate.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMvpDelegate.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mMvpDelegate.onAttach()
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

    override fun inject(injector: (Context) -> Unit) {
        injector(requireContext())
    }

    @ProvidePresenter
    fun provides() = PresenterExit()

}