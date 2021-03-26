package com.tesk.task.app.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.tesk.task.R
import com.tesk.task.app.Application
import com.tesk.task.app.ui.IUserController
import com.tesk.task.app.viewmodels.no.AuthorizeViewModel
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

object DialogLogin : DialogFragment(){

    lateinit var iUserController: IUserController

    lateinit var api : IApiGitJoke
    @Inject set

    lateinit var bd : AppDatabase
    @Inject set

    private lateinit var authorizeViewModel: AuthorizeViewModel

    private var loginString : String = ""
    private var passwordString : String = ""

    private lateinit var title : TextView
    private lateinit var login : EditText
    private lateinit var password : EditText
    private lateinit var cancel : Button
    private lateinit var enter : Button
    private lateinit var loading : ProgressBar

    private lateinit var buttonBar : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as Application).appComponent.inject(this)
        authorizeViewModel = AuthorizeViewModel(api, bd.myPageDao())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = view.findViewById(R.id.text_one)
        login = view.findViewById(R.id.login)
        password = view.findViewById(R.id.password)
        cancel = view.findViewById(R.id.button1)
        enter = view.findViewById(R.id.button2)
        loading = view.findViewById(R.id.loading)
        buttonBar = view.findViewById(R.id.button_bar)

        login.setText(loginString)
        password.setText(passwordString)
        password.inputType = InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD

        cancel.setText(R.string.cancel)
        cancel.setOnClickListener {
            dismiss()
        }

        enter.setText(R.string.enter)
        enter.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                authorizeViewModel.auth(loginString, passwordString)
            }
        }

        subscribe()
    }

    private fun showLoading(){
        loading.visibility = View.VISIBLE
        buttonBar.visibility = View.INVISIBLE
    }

    private fun hideLoading(){
        loading.visibility = View.INVISIBLE
        buttonBar.visibility = View.VISIBLE
    }

    private fun subscribe(){
        authorizeViewModel.liveData.observe(this, Observer {
            val result = it?:return@Observer

            if (result.first != null){
                try {
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.hideSoftInputFromWindow(requireActivity().getWindow().getDecorView().getWindowToken(), 0)
                }catch (e : java.lang.Exception){
                    e.printStackTrace()
                }

                iUserController.showUser()

                dismiss()
            } else if (result.second != null) {
                showLoading()
            } else if (result.third != null){
                Toast.makeText(requireContext(), getString(result.third?:return@Observer), Toast.LENGTH_SHORT).show()
                hideLoading()
            }
        })

        login.afterTextChanged {
            loginString = it
        }
        password.afterTextChanged {
            passwordString = it
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context).show()
        builder.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.dialog_rounder_8_white))
        builder.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        builder.window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        builder.window?.setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return builder
    }

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit){
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged.invoke(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }
}