package com.tesk.task.app.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tesk.task.R
import com.tesk.task.app.Application
import com.tesk.task.app.adapters.IShowUserHub
import com.tesk.task.app.adapters.SearchAdapter
import com.tesk.task.app.afterTextChanged
import com.tesk.task.app.ui.dialogs.DialogExit
import com.tesk.task.app.viewmodels.*
import com.tesk.task.providers.git.models.User
import kotlinx.android.synthetic.main.item_inner_search_t_result.*
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_item.*
import javax.inject.Inject

class SearchFragment : Fragment() {

    companion object{
        private val REQUEST_CODE = 123
        val RESULT_CODE = 321
        val RESULT_CODE_ERROR = 213
    }

    lateinit var viewModelFactory: FactoryViewModel
    @Inject set

    lateinit var iShowUserHub: IShowUserHub

    private val viewModel by lazy{
        viewModelFactory.create(ViewModelSearch::class.java)
    }

    private lateinit var searchAdapter: SearchAdapter

    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as Application).appComponent.inject(this)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = SearchAdapter(LayoutInflater.from(requireContext()), {user-> showRepository(user) })
        recycler_view.adapter = searchAdapter

        search_icon.setOnClickListener {
            search()
        }

        search.setText(query)
        search.imeOptions = EditorInfo.IME_ACTION_SEARCH
        search.setOnEditorActionListener { v, actionId, event ->
            when(actionId){
                EditorInfo.IME_ACTION_SEARCH -> {
                    search()
                }
            }
            false
        }
        search.afterTextChanged {
            query = it
        }

        login.setOnClickListener {
            gitOauth()
        }

        subscribe()
    }

    private fun gitOauth(){
        viewModel.login(getString(R.string.github_app_id), {intent->
            startActivityForResult(intent, 1000)
        })
    }

    private fun subscribe(){
        viewModel.liveDataUsers.observe(this, { list-> showUsers(list) })
        viewModel.liveDataLoading.observe(this, { isVisible-> if (isVisible) showLoading() })
        viewModel.liveDataError.observe(this, { showError() })
        viewModel.liveDataIsEmptyQuery.observe(this, { showOnEmptyQuery() })
        viewModel.liveDataIsEmptyList.observe(this, { showOnEmpty() })

        viewModel.liveDataShowStartMessage.observe(this, { showStart() })

        viewModel.liveDataApiException.observe(this, { showOnApiException() })
        viewModel.liveDataShowUser.observe(this, { name-> showMyFace(name) })
        viewModel.liveDataHideUser.observe(this, { hideMyFace() })
    }

    private fun search() {
        try {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(requireActivity().getWindow().getDecorView().getWindowToken(), 0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        viewModel.search(query)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
            viewModel.getMyProfile(requireContext())
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getMyProfile(requireContext())

        val intent = requireActivity().intent
        if (intent?.data?.toString()?.startsWith(getString(R.string.github_app_url)) == true) {
            viewModel.getAccessTokenGitHub(
                    intent,
                    getString(R.string.github_app_id),
                    getString(R.string.github_app_secret), requireContext())
        }
    }

    private fun showRepository(user: User){
        iShowUserHub.showRepo(user)
    }

    private fun hideMyFace() {
        login.setText(R.string.enter)
        box_of_my_account.visibility = View.GONE

        login.setOnClickListener {
            gitOauth()
        }
    }

    private fun showMyFace(name : String){
        login.setText(R.string.logout)
        box_of_my_account.visibility = View.VISIBLE

        title_user_name.text = String.format(getText(R.string.welcome).toString(), name)
        login.setOnClickListener {
            val dialog= DialogExit()
            dialog.setTargetFragment(this, REQUEST_CODE)
            showDialog(dialog)
        }
    }

    private fun showDialog(dialogFragment: DialogFragment){
        dialogFragment.show(fragmentManager?.beginTransaction()?:return, "")
    }

    private fun showUsers(list : List<User>){
        loading.visibility = View.GONE
        recycler_view.visibility = View.VISIBLE
        inner_frame.visibility = View.GONE

        searchAdapter.refresh(list)
        recycler_view.adapter?.notifyDataSetChanged()
    }

    private fun showLoading(){
        loading.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.GONE
    }

    private fun showOnEmptyQuery(){
        Toast.makeText(requireContext(), R.string.write_anything, Toast.LENGTH_SHORT).show()
    }

    private fun showOnApiException(){
        Toast.makeText(requireContext(), R.string.count_of_requests_get_a_higher_rate_limit, Toast.LENGTH_SHORT).show()
    }

    private fun showOnEmpty(){
        showInner(R.string.empty_result_query)
    }

    private fun showStart(){
        showInner(R.string.start)
    }

    private fun showError(){
        showInner(R.string.internet_access_worn)
    }

    private fun showInner(resource : Int){
        loading.visibility = View.GONE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.VISIBLE
        text.setText(resource)
    }

}