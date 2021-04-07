package com.tesk.task.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tesk.task.R
import com.tesk.task.app.Application
import com.tesk.task.app.adapters.RepositoryAdapter
import com.tesk.task.app.viewmodels.FactoryViewModel
import com.tesk.task.app.viewmodels.ViewModelRepository
import com.tesk.task.providers.git.models.Hub
import com.tesk.task.providers.git.models.User
import kotlinx.android.synthetic.main.item_inner_search_t_result.*
import kotlinx.android.synthetic.main.item_user.*
import kotlinx.android.synthetic.main.repository_fragment.*
import javax.inject.Inject

class HubFragment : Fragment() {

    lateinit var viewModuleFactory: FactoryViewModel
        @Inject set

    private val viewModel by lazy {
        viewModuleFactory.create(ViewModelRepository::class.java)
    }

    lateinit var user : User

    private lateinit var repositoryAdapter : RepositoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as Application).appComponent.inject(this)
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

        subscribe()

        viewModel.getRepositories(user)
    }

    private fun showInner(resource : Int){
        loading.visibility = View.GONE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.VISIBLE
        text.setText(resource)
    }

    private fun showError(){
        showInner(R.string.internet_access_worn)
    }

    private fun showLoading(){
        loading.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.GONE
    }

    private fun showHubs(list : List<Hub>){
        loading.visibility = View.GONE
        recycler_view.visibility = View.VISIBLE
        inner_frame.visibility = View.GONE

        repositoryAdapter.refresh(list)
        recycler_view.adapter?.notifyDataSetChanged()
    }

    private fun showOnEmpty(){
        showInner(R.string.user_have_not_repositories)
    }

    private fun showOnApiException(){
        Toast.makeText(requireContext(), R.string.count_of_requests_get_a_higher_rate_limit, Toast.LENGTH_SHORT).show()
    }

    private fun subscribe(){
        viewModel.apply {
            liveDataRepositories.observe(this@HubFragment, { list-> showHubs(list) })
            liveDataLoading.observe(this@HubFragment, { showLoading() })
            liveDataError.observe(this@HubFragment, { showError() })
            liveDataOnEmptyList.observe(this@HubFragment, { showOnEmpty() })
            liveDataApiException.observe(this@HubFragment, { showOnApiException() })
        }
    }
}