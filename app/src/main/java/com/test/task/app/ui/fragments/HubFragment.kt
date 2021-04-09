package com.test.task.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.task.R
import com.test.task.app.Application
import com.test.task.app.adapters.RepositoryAdapter
import com.test.task.app.mvp.presenters.PresenterHub
import com.test.task.app.mvp.views.IHubView
import com.test.task.providers.git.models.Hub
import com.test.task.providers.git.models.User
import kotlinx.android.synthetic.main.item_inner_search_t_result.*
import kotlinx.android.synthetic.main.repository_fragment.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class HubFragment : MvpAppCompatFragment(), IHubView {

    @Inject
    @InjectPresenter
    lateinit var presenter : PresenterHub

    lateinit var user : User

    private lateinit var repositoryAdapter : RepositoryAdapter

    @ProvidePresenter
    fun provide() = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireContext().applicationContext as Application).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        retainInstance = true
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
}