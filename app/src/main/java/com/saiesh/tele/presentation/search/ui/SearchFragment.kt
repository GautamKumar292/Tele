package com.saiesh.tele.presentation.search.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saiesh.tele.R
import com.saiesh.tele.app.MainActivity
import com.saiesh.tele.presentation.search.adapter.SearchResultsAdapter
import com.saiesh.tele.presentation.search.vm.SearchViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var adapter: SearchResultsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val queryInput = view.findViewById<EditText>(R.id.search_query)
        val searchButton = view.findViewById<Button>(R.id.search_button)
        val closeButton = view.findViewById<Button>(R.id.search_close)
        val progress = view.findViewById<ProgressBar>(R.id.search_progress)
        val message = view.findViewById<TextView>(R.id.search_message)
        val results = view.findViewById<RecyclerView>(R.id.search_results)

        adapter = SearchResultsAdapter { result ->
            viewModel.selectResult(result)
        }
        results.layoutManager = LinearLayoutManager(requireContext())
        results.adapter = adapter

        queryInput.addTextChangedListener { viewModel.updateQuery(it?.toString().orEmpty()) }
        searchButton.setOnClickListener { viewModel.performSearch(queryInput.text.toString()) }
        closeButton.setOnClickListener { (activity as? MainActivity)?.showBrowse() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    progress.isVisible = state.isSearching
                    val errorText = when {
                        state.error != null -> state.error
                        state.results.isEmpty() && !state.isSearching -> getString(R.string.search_empty)
                        else -> null
                    }
                    message.text = errorText.orEmpty()
                    message.isVisible = !errorText.isNullOrBlank()
                    if (queryInput.text.toString() != state.query) {
                        queryInput.setText(state.query)
                    }
                    adapter.submit(state.results)
                }
            }
        }
    }
}
