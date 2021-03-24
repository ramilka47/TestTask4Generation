package com.tesk.task.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tesk.task.R
import com.tesk.task.providers.api.impl.models.Repository
import java.lang.ref.WeakReference

class RepositoryAdapter(private val context : WeakReference<Context>) : RecyclerView.Adapter<RepositoryAdapter.RepoHolder>() {

    private var repositories = mutableListOf<Repository>()
    private var inflater = LayoutInflater.from(context.get())

    fun refresh(list : List<Repository>){
        repositories.clear()
        repositories.addAll(list)
    }

    override fun getItemCount(): Int = repositories.size

    override fun onBindViewHolder(holder: RepoHolder, position: Int) {
        holder.bind(repositories[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoHolder =
            RepoHolder(inflater.inflate(R.layout.item_repo, parent, false))

    inner class RepoHolder(view : View) : RecyclerView.ViewHolder(view){

        fun bind(repository: Repository){
            val title = itemView.findViewById<TextView>(R.id.title)
            val desctiption = itemView.findViewById<TextView>(R.id.desctiption)
            val lastCommit = itemView.findViewById<TextView>(R.id.last_commit)
            val fork = itemView.findViewById<TextView>(R.id.fork)
            val countOfForks = itemView.findViewById<TextView>(R.id.count_forks)
            val language = itemView.findViewById<TextView>(R.id.language)
            val stars = itemView.findViewById<TextView>(R.id.stars)

            title.setText(repository.name)
            val descriptionString = repository.desctiption
            if (descriptionString.isNullOrEmpty()){
                desctiption.visibility = View.GONE
            } else {
                desctiption.visibility = View.VISIBLE
                desctiption.setText(descriptionString)
            }
            lastCommit.text = String.format(itemView.context.getString(R.string.last_commit), repository.lastCommit.toString())
            fork.text = String.format(itemView.context.getString(R.string.default_branch), repository.currentFork)
            countOfForks.text = String.format(itemView.context.getString(R.string.forks), repository.countOfFork.toString())
            language.text = String.format(itemView.context.getString(R.string.language), repository.language)
            stars.text = String.format(itemView.context.getString(R.string.rating), repository.rating.toString())
        }
    }
}