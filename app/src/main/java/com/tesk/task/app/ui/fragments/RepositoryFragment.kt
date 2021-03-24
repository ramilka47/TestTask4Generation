package com.tesk.task.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tesk.task.R
import com.tesk.task.app.Application
import com.tesk.task.app.adapters.RepositoryAdapter
import com.tesk.task.app.viewmodels.GetHubViewModel
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.api.impl.models.Repository
import com.tesk.task.providers.api.impl.models.User
import com.tesk.task.providers.room.AppDatabase
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import javax.inject.Inject

class RepositoryFragment : Fragment() {

    lateinit var bd : AppDatabase
    @Inject set

    lateinit var api : IApiGitJoke
    @Inject set

    lateinit var user : User
    private lateinit var getHubViewModel : GetHubViewModel

    // вытащить из бандла
    private val TAG_GET_REPO = "tag_repo"
    private val TAG_GET_USE = "tag_user"

    private lateinit var title : TextView
    private lateinit var adapter : RepositoryAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var loading : ProgressBar
    private lateinit var innerResultFrame : FrameLayout
    private lateinit var innerResultText : TextView

    private var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as Application).appComponent.inject(this)
        getHubViewModel = GetHubViewModel(bd.hubDao(), api)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.repository_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = view.findViewById(R.id.name)
        recyclerView = view.findViewById(R.id.recycler_view)
        loading = view.findViewById(R.id.loading)
        innerResultFrame = view.findViewById(R.id.inner_frame)
        innerResultText = view.findViewById(R.id.text)

        adapter = RepositoryAdapter(WeakReference(requireContext()))
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        title.text = String.format(getString(R.string.user_s), user.name)

        getData()
        subscribe()
    }

    private fun getData(){
        if (job != null){
            job?.cancel()
        }

        job = CoroutineScope(Dispatchers.IO).launch {
            getHubViewModel.getRepository(user)
        }
    }

    private fun showInner(resource : Int){
        loading.visibility = View.GONE
        recyclerView.visibility = View.GONE
        innerResultFrame.visibility = View.VISIBLE
        innerResultText.setText(resource)
    }

    private fun showError(){
        showInner(R.string.internet_access_worn)
    }

    private fun showLoading(){
        loading.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        innerResultFrame.visibility = View.GONE
    }

    private fun updateList(list : List<Repository>){
        loading.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        innerResultFrame.visibility = View.GONE

        adapter.refresh(list)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showOnEmpty(){
        showInner(R.string.user_have_not_repositories)
    }

    private fun subscribe(){
        getHubViewModel.liveData.observe(this, Observer {
            val result = it?:return@Observer
            if (result.first != null){
                if (result.first.isNullOrEmpty()){
                    showOnEmpty()
                } else {
                    updateList(result.first?:return@Observer)
                }
            } else if (result.second != null){
                showLoading()
            } else if (result.third != null){
                if (result.third == R.string.count_of_requests_get_a_higher_rate_limit){
                    Toast.makeText(requireContext(), getString(result.third?:return@Observer), Toast.LENGTH_SHORT).show()
                } else {
                    showError()
                }
            }
        })
    }
}