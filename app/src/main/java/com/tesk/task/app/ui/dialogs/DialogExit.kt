package com.tesk.task.app.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.tesk.task.R
import com.tesk.task.app.Application
import com.tesk.task.app.ui.fragments.SearchFragment
import com.tesk.task.app.viewmodels.FactoryViewModel
import com.tesk.task.app.viewmodels.ViewModelLogout
import kotlinx.android.synthetic.main.dialog_exit.*
import javax.inject.Inject

class DialogExit : DialogFragment() {

    lateinit var viewModelFactory : FactoryViewModel
        @Inject set

    private val viewModel by lazy {
        viewModelFactory.create(ViewModelLogout::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as Application).appComponent.inject(this)
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
            viewModel.logout()
        }

        subscribe()
    }

    private fun onSuccess(){
        targetFragment?.onActivityResult(targetRequestCode, SearchFragment.RESULT_CODE, null)
        dismiss()
    }

    private fun showLoading(){
    }

    private fun showError(){
        targetFragment?.onActivityResult(targetRequestCode, SearchFragment.RESULT_CODE_ERROR, null)
        dismiss()
    }

    private fun subscribe(){
        viewModel.liveDataSuccess.observe(this, {
            onSuccess()
        })
        viewModel.liveDataLoading.observe(this, {
            showLoading()
        })
        viewModel.liveDataError.observe(this, {
            showError()
        })
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