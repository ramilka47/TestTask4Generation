package com.tesk.task.app.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.tesk.task.R
import com.tesk.task.app.Application
import com.tesk.task.app.viewmodels.FactoryViewModel
import com.tesk.task.app.viewmodels.ViewModelLogOut
import javax.inject.Inject

class DialogExit : DialogFragment() {

    lateinit var factoryViewModel : FactoryViewModel
    @Inject set

    private val viewModelLogOut by lazy {
        factoryViewModel.create(ViewModelLogOut::class.java)
    }

    private lateinit var title : TextView
    private lateinit var desctiption : TextView
    private lateinit var cancel : Button
    private lateinit var logOut : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as Application).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_exit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = view.findViewById(R.id.text_one)
        desctiption = view.findViewById(R.id.text_two)
        cancel = view.findViewById(R.id.button1)
        logOut = view.findViewById(R.id.button2)

        title.setText(R.string.you_sure_for_exit)
        desctiption.setText(R.string.all_info_you_profile_will_be_to_lose)

        cancel.setText(R.string.cancel)
        cancel.setOnClickListener {
            dismiss()
        }

        logOut.setText(R.string.logout)
        logOut.setOnClickListener {
            viewModelLogOut.logOut()
        }

        subscribe()
    }

    private fun onSuccess(){
        dismiss()
    }

    private fun showLoading(){
        //todo
    }

    private fun showError(){
        dismiss()
        //todo
    }

    private fun subscribe(){
        viewModelLogOut.exitLiveData.observe(this, {
            onSuccess()
        })
        viewModelLogOut.loadingLiveData.observe(this, {
            showLoading()
        })
        viewModelLogOut.errorLiveData.observe(this, {
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