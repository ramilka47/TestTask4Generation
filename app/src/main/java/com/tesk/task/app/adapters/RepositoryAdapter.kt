package com.tesk.task.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tesk.task.R
import com.tesk.task.providers.api.impl.models.Hub
import kotlinx.android.synthetic.main.item_repo.view.*

class RepositoryAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<RepositoryAdapter.RepoHolder>() {

    private var repositories = mutableListOf<Hub>()

    fun refresh(list : List<Hub>){
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

        fun bind(repository: Hub){
            itemView.title.setText(repository.name)

            val descriptionString = repository.desctiption
            if (descriptionString.isNullOrEmpty()){
                itemView.desctiption.visibility = View.GONE
            } else {
                itemView.desctiption.visibility = View.VISIBLE
                itemView.desctiption.setText(descriptionString)
            }
            itemView.last_commit.text = String.format(itemView.context.getString(R.string.last_commit), repository.lastCommit.toString())
            itemView.fork.text = String.format(itemView.context.getString(R.string.default_branch), repository.currentFork)
            itemView.count_forks.text = String.format(itemView.context.getString(R.string.forks), repository.countOfFork.toString())
            itemView.language.text = String.format(itemView.context.getString(R.string.language), repository.language)
            itemView.stars.text = String.format(itemView.context.getString(R.string.rating), repository.rating.toString())
        }
    }
}