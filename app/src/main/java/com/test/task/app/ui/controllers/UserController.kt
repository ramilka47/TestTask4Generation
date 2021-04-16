package com.test.task.app.ui.controllers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.RouterTransaction
import com.test.task.R
import com.test.task.app.Application
import com.test.task.app.adapters.SearchAdapter
import com.test.task.app.mvp.presenters.PresenterSearch
import com.test.task.app.mvp.views.ISearchView
import com.test.task.app.ui.dialogs.DialogExitController
import com.test.task.app.ui.dialogs.DialogExitUtil
import com.test.task.app.ui.dialogs.helpfull.SimpleDialogChangeHandler
import com.test.task.providers.git.models.User
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.newInstance

class UserController : MvpController(), ISearchView, KodeinAware {

    companion object{
        private const val QUERY = "UserController.query"
        private const val DIALOG = "Dialog"
        private const val DIALOG_EXIT = "DialogExitController"
        private const val HUB = "HubController"
    }

    override val kodein: Kodein by lazy{
        (applicationContext as Application).kodein
    }

    private val newInstance by newInstance { PresenterSearch(instance(), instance(), instance()) }

    @InjectPresenter
    lateinit var presenter : PresenterSearch

    private lateinit var searchAdapter: SearchAdapter

    @ProvidePresenter
    fun provide(): PresenterSearch = newInstance

    private lateinit var recycler_view : RecyclerView
    private lateinit var search_icon : ImageView
    private lateinit var search : EditText
    private lateinit var login : Button
    private lateinit var loading : ProgressBar
    private lateinit var inner_frame : View
    private lateinit var text_inner : TextView
    private lateinit var box_of_my_account : View
    private lateinit var title_user_name : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.search_fragment, container, false)
        attachViews(view)
        return view
    }

    override fun onRestoreViewState(view: View, savedViewState: Bundle) {
        super.onRestoreViewState(view, savedViewState)
        val trueQuery = savedViewState.getString(QUERY)
        trueQuery?.let { text ->
            search.setText(text, TextView.BufferType.EDITABLE)
        }
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
        outState.putString(QUERY, search.text.toString())
    }

    private fun attachViews(view : View){
        with(view){
            recycler_view = findViewById(R.id.recycler_view)
            search_icon = findViewById(R.id.search_icon)
            search = findViewById(R.id.search)
            login = findViewById(R.id.login)
            loading = findViewById(R.id.loading)
            inner_frame = findViewById(R.id.inner_frame)
            text_inner = findViewById(R.id.text_inner)
            box_of_my_account = findViewById(R.id.box_of_my_account)
            title_user_name = findViewById(R.id.title_user_name)

            searchAdapter = SearchAdapter(LayoutInflater.from(context)) { _user ->
                router.pushController(RouterTransaction.with(HubController().apply { user = _user }))
            }

            recycler_view.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = searchAdapter
            }
        }

        search_icon.setOnClickListener {
            search()
        }

        search.apply {
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        search()
                    }
                }
                false
            }
        }

        login.setOnClickListener {
            gitOAuth()
        }
    }

    private fun search() {
        try {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(
                activity?.window?.decorView?.windowToken, 0
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        presenter.search(search.text.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DialogExitUtil.REQUEST_CODE && resultCode == DialogExitUtil.RESULT_CODE) {
            presenter.getMyProfile()
        }
    }

    override fun showUsers(list: List<User>){
        loading.visibility = View.GONE
        recycler_view.visibility = View.VISIBLE
        inner_frame.visibility = View.GONE

        searchAdapter.refresh(list)
        recycler_view.adapter?.notifyDataSetChanged()
    }

    private fun gitOAuth() {
        getString(R.string.github_app_id)?.let {
            presenter.login(
                it
            ) { intent ->
                startActivityForResult(intent, 1000)
            }
        }
    }

    override fun showMyAccount(name: String) {
        login.setText(R.string.logout)
        box_of_my_account.visibility = View.VISIBLE

        title_user_name.text = String.format(getString(R.string.welcome).toString(), name)
        login.setOnClickListener {
            router.pushController(
                RouterTransaction.with(
                    DialogExitController()
                        .apply {
                            targetController = this
                        })
                    .pushChangeHandler(SimpleDialogChangeHandler())
                    .pushChangeHandler(SimpleDialogChangeHandler())
            )
        }
    }

    override fun hideMyAccount() {
        login.setText(R.string.enter)
        box_of_my_account.visibility = View.GONE

        login.setOnClickListener {
            gitOAuth()
        }
    }

    override fun showOnEmptyQuery() {
        Toast.makeText(activity, R.string.write_anything, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading(){
        loading.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.GONE
    }

    override fun showEmptyUsers() {
        showInner(R.string.empty_result_query)
    }

    override fun showErrorApiRequestRate() {
        Toast.makeText(
            activity,
            R.string.count_of_requests_get_a_higher_rate_limit,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showStart() {
        showInner(R.string.start)
    }

    override fun showErrorInternetAccess() {
        showInner(R.string.internet_access_worn)
    }

    private fun showInner(resource: Int){
        loading.visibility = View.GONE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.VISIBLE
        text_inner.setText(resource)
    }

    override fun intent(intent: (Intent, githubUrl: String, gitId: String, gitSecret: String) -> Unit) {
        activity?.intent?.let {
            getString(R.string.github_app_url)?.let { it1 ->
                getString(R.string.github_app_id)?.let { it2 ->
                    getString(R.string.github_app_secret)?.let { it3 ->
                        intent(it, it1, it2, it3)
                    }
                }
            }
        }
    }
}