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
import androidx.lifecycle.Observer
import com.tesk.task.R
import com.tesk.task.app.Application
import com.tesk.task.app.ui.IUserController
import com.tesk.task.app.viewmodels.models.ViewModelLogOut
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

object DialogExit : DialogFragment() {

    private lateinit var title : TextView
    private lateinit var desctiption : TextView
    private lateinit var cancel : Button
    private lateinit var logOut : Button

    lateinit var bd : AppDatabase
    @Inject set

    lateinit var api : IApiGitJoke
    @Inject set

    lateinit var viewModelLogOut: ViewModelLogOut
    lateinit var iUserController: IUserController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as Application).appComponent.inject(this)
        viewModelLogOut = ViewModelLogOut(bd.myPageDao(), api)
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
            CoroutineScope(Dispatchers.IO).launch {
                viewModelLogOut.logOut()
            }
        }

        subscribe()
    }

    private fun subscribe(){
        viewModelLogOut.liveData.observe(this, Observer {
            val result = it?:return@Observer

            if (result.first != null){
                iUserController.hideUser()
                dismiss()
            }
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