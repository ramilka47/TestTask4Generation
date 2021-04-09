package com.test.task.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.task.R
import com.test.task.providers.git.models.Hub
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
            with(repository) {
                itemView.apply {
                    title.text = repository.name

                    val descriptionString = repository.desctiption
                    if (descriptionString.isNullOrEmpty()) {
                        desctiption.visibility = View.GONE
                    } else {
                        desctiption.visibility = View.VISIBLE
                        desctiption.text = descriptionString
                    }
                    last_commit.text = String.format(context.getString(R.string.last_commit), lastCommit.toString())
                    fork.text = String.format(context.getString(R.string.default_branch), currentFork)
                    count_forks.text = String.format(context.getString(R.string.forks), countOfFork.toString())
                    language.text = String.format(context.getString(R.string.language), repository.language)
                    stars.text = String.format(context.getString(R.string.rating), rating.toString())
                }
            }
        }
    }
}