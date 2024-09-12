package com.test.task.app.ui.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.test.task.R
import com.test.task.app.Application
import com.test.task.app.mvp.presenters.PresenterExit
import com.test.task.app.mvp.views.IExitView
import com.test.task.app.ui.dialogs.helpfull.DialogController
import kotlinx.android.synthetic.main.dialog_exit.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.newInstance

class DialogExitController : DialogController(), IExitView, KodeinAware {

    override val kodein: Kodein by lazy{
        (applicationContext as Application).kodein
    }

    private val newInstance by newInstance { PresenterExit(instance()) }

    @InjectPresenter
    lateinit var presenter : PresenterExit

    @ProvidePresenter
    fun provides() = newInstance

    private lateinit var text_one : TextView
    private lateinit var text_two : TextView
    private lateinit var button1 : Button
    private lateinit var button2 : Button

    override fun onCreateContentView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        val view = inflater?.inflate(R.layout.dialog_exit,container, false)
        attachViews(view)
        return view
    }

    private fun attachViews(view: View?){
        view?.let{
            with(view){
                text_one = findViewById(R.id.text_one)
                text_two = findViewById(R.id.text_two)
                button1 = findViewById(R.id.button1)
                button2 = findViewById(R.id.button2)

                text_one.setText(R.string.you_sure_for_exit)
                text_two.setText(R.string.all_info_you_profile_will_be_to_lose)

                button1.setText(R.string.cancel)
                button1.setOnClickListener {
                    router.popController(this@DialogExitController)
                }

                button2.setText(R.string.logout)
                button2.setOnClickListener {
                    presenter.logout()
                }
            }
        }
    }

    override fun onExitError() {
        targetController?.onActivityResult(
            DialogExitUtil.REQUEST_CODE,
            DialogExitUtil.RESULT_CODE_ERROR, null)
        router.popController(this)
    }

    override fun onExitSuccess() {

        targetController?.onActivityResult(
            DialogExitUtil.REQUEST_CODE,
            DialogExitUtil.RESULT_CODE, null)
        router.popController(this)
    }

}