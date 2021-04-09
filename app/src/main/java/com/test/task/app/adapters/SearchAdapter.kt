package com.test.task.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.task.R
import com.test.task.providers.git.models.User
import kotlinx.android.synthetic.main.item_user.view.*

class SearchAdapter(private val inflater: LayoutInflater, private val showRepository : (user : User)->Unit) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var users = mutableListOf<User>()

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(inflater.inflate(R.layout.item_user, parent, false))

    fun refresh(list : List<User>){
        users.clear()
        users.addAll(list)
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener {
                onClickMainView()
            }
        }

        private fun onClickMainView(){
            val user = users[adapterPosition]
            showRepository(user)
        }

        fun bind(user: User) {
            itemView.apply {
                name.text = user.name
                followers.text = user.followers.toString()

                Glide
                        .with(context)
                        .load(user.avatar)
                        .circleCrop()
                        .into(avatar)

                followers.text = user.followers.toString()
            }
        }
    }
}