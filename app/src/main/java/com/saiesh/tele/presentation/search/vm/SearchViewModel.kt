package com.saiesh.tele.presentation.search.vm

import androidx.lifecycle.ViewModel
import com.saiesh.tele.data.repository.media.SavedMessagesRepository
import com.saiesh.tele.domain.model.media.MediaItem
import com.saiesh.tele.domain.model.search.SearchBotResponse
import com.saiesh.tele.domain.model.search.SearchQueryResult
import com.saiesh.tele.domain.model.search.SearchUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel(
    private val repository: SavedMessagesRepository = SavedMessagesRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    fun openOverlay() {
        _uiState.update { it.copy(isOverlayVisible = true, error = null) }
    }

    fun closeOverlay() {
        _uiState.update { it.copy(isOverlayVisible = false, error = null, results = emptyList(), query = "") }
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun performSearch(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(error = "Enter a search term") }
            return
        }
        _uiState.update { it.copy(isSearching = true, error = null, results = emptyList(), query = query) }
        repository.searchProBot(query) { response ->
            handleSearchResponse(response)
        }
    }

    fun selectResult(result: SearchQueryResult) {
        _uiState.update { it.copy(isSearching = true, error = null) }
        repository.submitProBotSelection(result) { response ->
            handleSearchResponse(response)
        }
    }

    fun consumeRefreshMedia(): Boolean {
        val shouldRefresh = _uiState.value.refreshMedia
        if (shouldRefresh) {
            _uiState.update { it.copy(refreshMedia = false) }
        }
        return shouldRefresh
    }

    private fun handleSearchResponse(response: SearchBotResponse) {
        when (response) {
            is SearchBotResponse.Results -> {
                val filteredResults = response.results.filterNot { result ->
                    result.title.contains("srt", ignoreCase = true)
                }
                _uiState.update { current ->
                    current.copy(isSearching = false, results = filteredResults, error = null)
                }
            }
            is SearchBotResponse.Error -> {
                _uiState.update { current -> current.copy(isSearching = false, error = response.message) }
            }
            is SearchBotResponse.Media -> saveToSavedMessages(response.item)
        }
    }

    private fun saveToSavedMessages(item: MediaItem) {
        _uiState.update { it.copy(isSearching = true, error = null) }
        repository.saveSearchMediaToSavedMessages(item) { error ->
            _uiState.update { current ->
                if (error != null) {
                    current.copy(isSearching = false, error = error)
                } else {
                    current.copy(
                        isSearching = false,
                        isOverlayVisible = false,
                        results = emptyList(),
                        refreshMedia = true
                    )
                }
            }
        }
    }
}
