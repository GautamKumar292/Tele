package com.saiesh.tele.presentation.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saiesh.tele.R
import com.saiesh.tele.domain.model.search.SearchQueryResult

class SearchResultsAdapter(
    private val onClick: (SearchQueryResult) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.ResultViewHolder>() {
    private val items = mutableListOf<SearchQueryResult>()

    fun submit(results: List<SearchQueryResult>) {
        items.clear()
        items.addAll(results)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ResultViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ResultViewHolder(
        itemView: View,
        private val onClick: (SearchQueryResult) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.search_result_title)

        fun bind(item: SearchQueryResult) {
            title.text = item.title
            itemView.setOnClickListener { onClick(item) }
        }
    }
}
