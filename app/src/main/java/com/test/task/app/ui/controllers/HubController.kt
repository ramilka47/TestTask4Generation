package com.test.task.app.ui.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.task.R
import com.test.task.app.Application
import com.test.task.app.adapters.RepositoryAdapter
import com.test.task.app.mvp.presenters.PresenterHub
import com.test.task.app.mvp.views.IHubView
import com.test.task.providers.git.models.Hub
import com.test.task.providers.git.models.User
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.newInstance

class HubController : MvpController(), IHubView, KodeinAware {

    override val kodein: Kodein by lazy {
        (applicationContext as Application).kodein
    }

    private val newInstance by newInstance{ PresenterHub(instance(), instance()) }

    @InjectPresenter
    lateinit var presenter : PresenterHub

    lateinit var user : User

    private lateinit var repositoryAdapter : RepositoryAdapter
    private lateinit var recycler_view : RecyclerView
    private lateinit var loading : ProgressBar
    private lateinit var inner_frame : View
    private lateinit var text_inner : TextView
    private lateinit var title : TextView

    @ProvidePresenter
    fun provide() = newInstance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.repository_fragment, container, false)
        attachViews(view)
        return view
    }

    private fun attachViews(view : View){
        with(view){
            title = findViewById(R.id.title_user_name)
            recycler_view = findViewById(R.id.recycler_view)
            loading = findViewById(R.id.loading)
            inner_frame = findViewById(R.id.inner_frame)
            text_inner = findViewById(R.id.text_inner)

            repositoryAdapter = RepositoryAdapter(LayoutInflater.from(context))
            recycler_view.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = repositoryAdapter
            }
            title.text = user.name
        }
    }

    private fun showInner(resource : Int){
        loading.visibility = View.GONE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.VISIBLE
        text_inner.setText(resource)
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
        Toast.makeText(activity, R.string.count_of_requests_get_a_higher_rate_limit, Toast.LENGTH_SHORT).show()
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