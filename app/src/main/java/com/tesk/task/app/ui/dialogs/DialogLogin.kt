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
import com.tesk.task.R
import com.tesk.task.app.Application
import com.tesk.task.app.viewmodels.FactoryViewModel
import com.tesk.task.app.viewmodels.ViewModelAuthorize
import com.tesk.task.app.viewmodels.ViewModelMyFace
import kotlinx.android.synthetic.main.dialog_login.*
import javax.inject.Inject

class DialogLogin : DialogFragment(){

    lateinit var viewModelFactory : FactoryViewModel
    @Inject set

    private val authorizeViewModel by lazy {
        viewModelFactory.create(ViewModelAuthorize::class.java)
    }

    private val viewModelMyFace by lazy {
        viewModelFactory.create(ViewModelMyFace::class.java)
    }

    private var loginString : String = ""
    private var passwordString : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as Application).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login.setText(loginString)
        password.setText(passwordString)
        password.inputType = InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD

        button1.setText(R.string.cancel)
        button1.setOnClickListener {
            dismiss()
        }

        button2.setText(R.string.enter)
        button2.setOnClickListener {
            authorizeViewModel.auth(loginString, passwordString)
        }

        login.afterTextChanged {
            loginString = it
        }
        password.afterTextChanged {
            passwordString = it
        }

        subscribe()
    }

    private fun showLoading(){
        loading.visibility = View.VISIBLE
        button_bar.visibility = View.INVISIBLE
    }

    private fun hideLoading(){
        loading.visibility = View.INVISIBLE
        button_bar.visibility = View.VISIBLE
    }

    private fun onSuccess(name : String){
        try {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(requireActivity().getWindow().getDecorView().getWindowToken(), 0)
        }catch (e : java.lang.Exception){
            e.printStackTrace()
        }

        Toast.makeText(requireContext(), String.format(getString(R.string.welcome), name), Toast.LENGTH_SHORT).show()

        viewModelMyFace.showMyFace(name)
        dismiss()
    }

    private fun onError(){
        /*Toast.makeText(requireContext(), , Toast.LENGTH_SHORT).show()*/
    }

    private fun subscribe(){
        authorizeViewModel.successLiveData.observe(this, { onSuccess(it) })
        authorizeViewModel.loadingLiveData.observe(this, {
            if (it) {
                showLoading()
            } else {
                hideLoading()
            }
        })
        authorizeViewModel.errorLiveData.observe(this, { onError() })
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