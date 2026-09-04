package com.example.mobile.model

data class WasteItem(
    val id: Int,
    val name: String,
    val category: WasteCategory,
    val disposalType: String,
    val recyclable: Boolean,
    val description: String,
    val steps: List<String>,
    val photoResId: Int,
    val reminder: String? = null
)
