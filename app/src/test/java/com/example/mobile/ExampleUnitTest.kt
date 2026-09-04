package com.example.mobile

import com.example.mobile.data.WasteData
import com.example.mobile.model.WasteCategory
import com.example.mobile.ui.viewmodel.WasteGuideViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun wasteItemCanBeFoundById() {
        assertEquals("Paper coffee cup", WasteData.getById(8)?.name)
    }

    @Test
    fun wasteGuideSearchFiltersTheVisibleItems() {
        val viewModel = WasteGuideViewModel()

        viewModel.onQueryChange("phone")

        assertEquals(listOf("Mobile phone"), viewModel.uiState.value.visibleItems.map { it.name })
    }

    @Test
    fun wasteGuideCategoryFilterUsesTheSelectedCategory() {
        val viewModel = WasteGuideViewModel()

        viewModel.onCategorySelected(WasteCategory.E_WASTE)

        assertTrue(viewModel.uiState.value.visibleItems.isNotEmpty())
        assertTrue(viewModel.uiState.value.visibleItems.all { it.category == WasteCategory.E_WASTE })
    }
}
