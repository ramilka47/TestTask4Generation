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
import com.tesk.task.app.viewmodels.*
import com.tesk.task.providers.api.impl.models.User
import kotlinx.android.synthetic.main.item_inner_search_t_result.*
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_item.*
import javax.inject.Inject

class SearchFragment : Fragment(), IGetFollowers {

    lateinit var viewModelFactory: FactoryViewModel
    @Inject set

    private val viewModel by lazy {
        viewModelFactory.create(AViewModel.SearchViewModel::class.java)
    }

    private val viewModelMyFace by lazy{
        viewModelFactory.create(ViewModelMyFace::class.java)
    }

    private val viewModelGetMyFace by lazy {
        viewModelFactory.create(ViewModelGetMyFace::class.java)
    }

    private val viewModelGetFollowers by lazy{
        viewModelFactory.create(ViewModelGetFollowers::class.java)
    }

    lateinit var iShowUserHub: IShowUserHub
    private lateinit var searchAdapter: SearchAdapter

    private var query = ""

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

        recycler_view.setHasFixedSize(true)
        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = SearchAdapter(LayoutInflater.from(requireContext()), iShowUserHub, this)
        recycler_view.adapter = searchAdapter

        search_icon.setOnClickListener {
            search()
        }

        login.setOnClickListener {
            showDialog(DialogLogin())
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

        showStart()
        subscribe()

        viewModelGetMyFace.get()
    }

    private fun showDialog(dialogFragment: DialogFragment){
        dialogFragment.show(childFragmentManager.beginTransaction(), "")
    }

    private fun hideMyFace() {
        login.setText(R.string.enter)
        val myFace = childFragmentManager.findFragmentByTag(TAG_MY_FACE)
        if (myFace != null){
            childFragmentManager.beginTransaction().remove(myFace).setCustomAnimations(R.anim.slide_out_top, R.anim.slide_in_top).commit()
        }

        login.setOnClickListener {
            showDialog(DialogLogin())
        }
    }

    private fun showMyFace(name : String){
        login.setText(R.string.logout)
        val myFaceFragment = MyFaceFragment()
        myFaceFragment.setName(name)
        childFragmentManager.beginTransaction().add(R.id.container, myFaceFragment, TAG_MY_FACE).setCustomAnimations(R.anim.slide_out_bottom, R.anim.slide_in_top).commit()
        login.setOnClickListener {
            showDialog(DialogExit())
        }
    }

    private fun showUsers(list : List<User>){
        loading.visibility = View.GONE
        recycler_view.visibility = View.VISIBLE
        inner_frame.visibility = View.GONE

        searchAdapter.refresh(list)
        recycler_view.adapter?.notifyDataSetChanged()
    }

    private fun showOnEmpty(){
        showInner(R.string.empty_result_query)
    }

    private fun showInner(resource : Int){
        loading.visibility = View.GONE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.VISIBLE
        text.setText(resource)
    }

    private fun showStart(){
        showInner(R.string.start)
    }

    private fun showError(){
        showInner(R.string.internet_access_worn)
    }

    private fun showLoading(){
        loading.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE
        inner_frame.visibility = View.GONE
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

        viewModelMyFace.showMyFaceLiveData.observe(this, { showMyFace(it?:return@observe) })
        viewModelMyFace.hideMyFaceLiveData.observe(this, { hideMyFace() })

        viewModelGetMyFace.successLiveData.observe(this, { showMyFace(it) })

        viewModelGetFollowers.followersLiveData.observe(this, {

        })
    }

    private fun search() {
        val query = search.text.toString()
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

    override fun getForUser(user: User) {
        viewModelGetFollowers.getFollowers(user)
    }
}