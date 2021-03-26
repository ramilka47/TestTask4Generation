package com.tesk.task.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tesk.task.R
import com.tesk.task.providers.api.impl.models.User

class SearchAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var users = mutableListOf<User>()

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

            name.setText(user.name)
            followers.setText(user.followers.toString())

            Glide
                    .with(itemView.context)
                    .load(user.avatar)
                    .circleCrop()
                    .into(avatar)

            /*itemView.setOnClickListener {
                iShowRepository.showRepo(user)
            }*/

            /*CoroutineScope(Dispatchers.Main).launch {
            }

            if (user.followers == 0) {
             *//*   CoroutineScope(Dispatchers.IO).launch {
                    val res =
                            try {
                                GetFollowersViewModel.getFollowers(userDao, api, user)
                            }catch (e : IOException){
                                0
                            }.toString()
                    followers.post {
                        followers.setText(res)
                    }
                }*//*
            } else {
                 itemView.findViewById<TextView>(R.id.followers).text = user.followers.toString()
            }*/
        }
    }
}