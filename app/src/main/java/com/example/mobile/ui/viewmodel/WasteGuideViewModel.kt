package com.example.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobile.data.WasteData
import com.example.mobile.model.WasteCategory
import com.example.mobile.model.WasteItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WasteGuideUiState(
    val query: String = "",
    val selectedCategory: WasteCategory = WasteCategory.ALL,
    val visibleItems: List<WasteItem> = WasteData.wasteList
)

class WasteGuideViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WasteGuideUiState())
    val uiState: StateFlow<WasteGuideUiState> = _uiState.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        filterItems()
    }

    fun onCategorySelected(newCategory: WasteCategory) {
        _uiState.update { it.copy(selectedCategory = newCategory) }
        filterItems()
    }

    fun clearFilters() {
        _uiState.value = WasteGuideUiState()
    }

    private fun filterItems() {
        val query = _uiState.value.query.trim()
        val category = _uiState.value.selectedCategory

        val filtered = WasteData.wasteList.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true) ||
                item.disposalType.contains(query, ignoreCase = true)
            val matchesCategory = category == WasteCategory.ALL || item.category == category
            matchesQuery && matchesCategory
        }

        _uiState.update { it.copy(visibleItems = filtered) }
    }
}
