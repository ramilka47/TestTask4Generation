package com.tesk.task.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tesk.task.R
import com.tesk.task.app.viewmodels.no.GetFollowersViewModel
import com.tesk.task.providers.api.IApiGitJoke
import com.tesk.task.providers.api.impl.models.User
import com.tesk.task.providers.room.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.ref.WeakReference

class SearchAdapter(private val iShowRepository: IShowUserHub,
                    private val context : WeakReference<Context>,
                    private val userDao: UserDao,
                    private val api : IApiGitJoke) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var users = mutableListOf<User>()
    private val inflater = LayoutInflater.from(context.get())

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(inflater.inflate(R.layout.item_user, parent, false))

    fun refresh(list : List<User>){
        users.clear()
        users.addAll(list)
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        fun bind(user: User){
            val avatar = itemView.findViewById<ImageView>(R.id.avatar)
            val name = itemView.findViewById<TextView>(R.id.name)
            val followers = itemView.findViewById<TextView>(R.id.followers)
            name.text = user.name

            itemView.setOnClickListener {
                iShowRepository.showRepo(user)
            }

            CoroutineScope(Dispatchers.Main).launch {
                Glide
                        .with(itemView.context)
                        .load(user.avatar)
                        .circleCrop()
                        .into(avatar)
            }

            if (user.followers == 0) {
                CoroutineScope(Dispatchers.IO).launch {
                    val res =
                            try {
                                GetFollowersViewModel.getFollowers(userDao, api, user)
                            }catch (e : IOException){
                                0
                            }.toString()
                    followers.post {
                        followers.setText(res)
                    }
                }
            } else {
                 itemView.findViewById<TextView>(R.id.followers).text = user.followers.toString()
            }
        }
    }
}