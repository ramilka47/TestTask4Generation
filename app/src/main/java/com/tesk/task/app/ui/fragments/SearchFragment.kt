package com.tesk.task.app.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tesk.task.R
import com.tesk.task.app.Application
import com.tesk.task.app.adapters.IShowUserHub
import com.tesk.task.app.adapters.SearchAdapter
import com.tesk.task.app.ui.dialogs.DialogExit
import com.tesk.task.app.ui.dialogs.DialogLogin
import com.tesk.task.app.viewmodels.AViewModel
import com.tesk.task.app.viewmodels.FactoryViewModel
import com.tesk.task.app.viewmodels.ViewModelMyFace
import com.tesk.task.providers.api.impl.models.User
import java.lang.ref.WeakReference
import javax.inject.Inject

class SearchFragment : Fragment() {

    lateinit var viewModuleFactory: FactoryViewModel
    @Inject set

    private val viewModel by lazy {
        viewModuleFactory.create(AViewModel.SearchViewModel::class.java)
    }

    private val viewModelMyFace by lazy{
        viewModuleFactory.create(ViewModelMyFace::class.java)
    }

    private lateinit var recyclerView : RecyclerView
    private lateinit var loading : ProgressBar
    private lateinit var innerResultFrame : FrameLayout
    private lateinit var innerResultText : TextView
    private lateinit var editFieldSearch : EditText
    private lateinit var searchIcon : ImageView
    private lateinit var enterButton : Button

    lateinit var iShowUserRepositories: IShowUserHub
    private lateinit var searchAdapter: SearchAdapter

    private var query = ""
    private var faceId = 0

    private val TAG_MY_FACE = "my_face"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as Application).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        loading = view.findViewById(R.id.loading)
        innerResultFrame = view.findViewById(R.id.inner_frame)
        innerResultText = view.findViewById(R.id.text)
        editFieldSearch = view.findViewById(R.id.search)
        searchIcon = view.findViewById(R.id.search_icon)
        enterButton = view.findViewById(R.id.login)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = SearchAdapter(LayoutInflater.from(requireContext()))
        recyclerView.adapter = searchAdapter

        searchIcon.setOnClickListener {
            search()
        }

        enterButton.setOnClickListener {
            showDialog(DialogLogin())
        }

        editFieldSearch.setText(query)
        editFieldSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
        editFieldSearch.setOnEditorActionListener { v, actionId, event ->
            when(actionId){
                EditorInfo.IME_ACTION_SEARCH -> {
                    search()
                }
            }
            false
        }
        editFieldSearch.afterTextChanged {
            query = it
        }

        showStart()
        subscribe()
    }

    private fun showDialog(dialogFragment: DialogFragment){
        dialogFragment.show(childFragmentManager.beginTransaction(), "")
    }

    private fun hideMyFace() {
        enterButton.setText(R.string.enter)
        val myFace = childFragmentManager.findFragmentByTag(TAG_MY_FACE)
        if (myFace != null){
            childFragmentManager.beginTransaction().remove(myFace).setCustomAnimations(R.anim.slide_out_top, R.anim.slide_in_top).commit()
        }

        enterButton.setOnClickListener {
            showDialog(DialogLogin())
        }
    }

    private fun showMyFace(name : String){
        enterButton.setText(R.string.logout)
        val myFaceFragment = MyFaceFragment()
        myFaceFragment.setName(name)
        faceId = myFaceFragment.id
        childFragmentManager.beginTransaction().add(R.id.container, myFaceFragment, TAG_MY_FACE).setCustomAnimations(R.anim.slide_out_bottom, R.anim.slide_in_top).commit()

        enterButton.setOnClickListener {
            showDialog(DialogExit())
        }
    }

    private fun showUsers(list : List<User>){
        loading.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        innerResultFrame.visibility = View.GONE

        searchAdapter.refresh(list)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showOnEmpty(){
        showInner(R.string.empty_result_query)
    }

    private fun showInner(resource : Int){
        loading.visibility = View.GONE
        recyclerView.visibility = View.GONE
        innerResultFrame.visibility = View.VISIBLE
        innerResultText.setText(resource)
    }

    private fun showStart(){
        showInner(R.string.start)
    }

    private fun showError(){
        showInner(R.string.internet_access_worn)
    }

    private fun showLoading(){
        loading.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        innerResultFrame.visibility = View.GONE
    }

    private fun subscribe(){
        viewModel.loadingLiveData.observe(this, { showLoading() })
        viewModel.isEmptyListLiveData.observe(this, { showOnEmpty() })
        viewModel.errorLiveData.observe(this, { /*error*/
            /*  if (result.third == R.string.count_of_requests_get_a_higher_rate_limit){
                        Toast.makeText(requireContext(), getString(result.third?:return@Observer), Toast.LENGTH_SHORT).show()
                    } else {
                        showError()
                    }*/
        })
        viewModel.dataLiveData.observe(this, { showUsers(it) })

       /* viewModelMyFace.successLiveData.observe(this, { showMyFace(it) })
        viewModelMyFace.errorLiveData.observe(this, { hideMyFace() })*/
    }

    private fun search() {
        val query = editFieldSearch.text.toString()
        if (query.isNullOrEmpty()) {
            Toast.makeText(requireContext(), R.string.write_anything, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(requireActivity().getWindow().getDecorView().getWindowToken(), 0)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        viewModel.getUsers(query)
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