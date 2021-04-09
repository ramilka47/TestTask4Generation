package com.test.task.app.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.task.R
import com.test.task.app.Application
import com.test.task.app.adapters.SearchAdapter
import com.test.task.app.ui.afterTextChanged
import com.test.task.app.mvp.presenters.PresenterSearch
import com.test.task.app.mvp.views.ISearchView
import com.test.task.app.ui.dialogs.DialogExit
import com.test.task.providers.git.models.User
import kotlinx.android.synthetic.main.item_inner_search_t_result.*
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_item.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class SearchFragment : MvpAppCompatFragment(), ISearchView {

    companion object{
        private const val REQUEST_CODE = 123
        const val RESULT_CODE = 321
        const val RESULT_CODE_ERROR = 213
    }

    @Inject
    @InjectPresenter
    lateinit var presenter : PresenterSearch

    private lateinit var searchAdapter: SearchAdapter

    private var query = ""

    @ProvidePresenter
    fun provide(): PresenterSearch = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireContext().applicationContext as Application).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchAdapter = SearchAdapter(LayoutInflater.from(requireContext())) { user ->
            val fragment = HubFragment().apply {
                this.user = user
            }
            fragmentManager?.let {
                it.beginTransaction().replace(R.id.container, fragment)
                    .addToBackStack(fragment.javaClass.name).commit()
            }
        }
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
             adapter = searchAdapter
        }

        search_icon.setOnClickListener {
            search()
        }

        search.apply {
            setText(query)
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        search()
                    }
                }
                false
            }
            afterTextChanged {
                query = it
            }
        }

        login.setOnClickListener {
            gitOAuth()
        }
    }

    private fun search() {
        try {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(
                requireActivity().window.decorView.windowToken, 0
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        presenter.search(query)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
            presenter.getMyProfile()
        }
    }

    private fun showDialog(dialogFragment: DialogFragment){
        dialogFragment.show(requireFragmentManager().beginTransaction(), dialogFragment.tag)
    }

    override fun showUsers(list: List<User>){
        loading.visibility = View.GONE
        recycler_view.visibility = View.VISIBLE
        inner_frame.visibility = View.GONE

        searchAdapter.refresh(list)
        recycler_view.adapter?.notifyDataSetChanged()
    }

    private fun gitOAuth() {
        presenter.login(
            getString(R.string.github_app_id)
        ) { intent ->
            startActivityForResult(intent, 1000)
        }
    }

    override fun showMyAccount(name: String) {
        login.setText(R.string.logout)
        box_of_my_account.visibility = View.VISIBLE

        title_user_name.text = String.format(getText(R.string.welcome).toString(), name)
        login.setOnClickListener {
            val dialog= DialogExit()
            dialog.setTargetFragment(this, REQUEST_CODE)
            showDialog(dialog)
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
        Toast.makeText(requireContext(), R.string.write_anything, Toast.LENGTH_SHORT).show()
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
            requireContext(),
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
        text.setText(resource)
    }

    override fun intent(intent: (Intent, githubUrl: String, gitId: String, gitSecret: String) -> Unit) {
        intent(requireActivity().intent, getString(R.string.github_app_url), getString(R.string.github_app_id), getString(R.string.github_app_secret))
    }
}