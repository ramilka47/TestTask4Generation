package com.tesk.task.app.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tesk.task.R
import com.tesk.task.app.adapters.RepositoryAdapter
import com.tesk.task.app.mvp.presenters.PresenterHub
import com.tesk.task.app.mvp.views.IHubView
import com.tesk.task.providers.git.models.Hub
import com.tesk.task.providers.git.models.User
import kotlinx.android.synthetic.main.item_inner_search_t_result.*
import kotlinx.android.synthetic.main.repository_fragment.*
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class HubFragment : Fragment(), IHubView {

    @InjectPresenter
    lateinit var presenter : PresenterHub

    lateinit var user : User

    private lateinit var repositoryAdapter : RepositoryAdapter

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
        return inflater.inflate(R.layout.repository_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repositoryAdapter = RepositoryAdapter(LayoutInflater.from(requireContext()))
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = repositoryAdapter
        }

        title_user_name.text = String.format(getString(R.string.user_s), user.name)
    }

    private fun showInner(resource : Int){
        loading.visibility = View.GONE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.VISIBLE
        text.setText(resource)
    }

    override fun showLoading(){
        loading.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.GONE
    }

    override fun showErrorInternetAccess() {
        showInner(R.string.internet_access_worn)
    }

    override fun showErrorApiRequestRate() {
        Toast.makeText(requireContext(), R.string.count_of_requests_get_a_higher_rate_limit, Toast.LENGTH_SHORT).show()
    }

    override fun showEmptyHubs() {
        showInner(R.string.user_have_not_repositories)
    }

    override fun showHubs(list : List<Hub>) {
        loading.visibility = View.GONE
        recycler_view.visibility = View.VISIBLE
        inner_frame.visibility = View.GONE

        repositoryAdapter.refresh(list)
        recycler_view.adapter?.notifyDataSetChanged()
    }

    override fun getUser(getUser: (User) -> Unit) {
        getUser(user)
    }

    override fun inject(injector: (Context) -> Unit) {
        injector(requireContext())
    }

    @ProvidePresenter
    fun provides() = PresenterHub()
}