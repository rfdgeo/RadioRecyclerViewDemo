package com.example.recyclerviewradiodemo.models

data class ParentParameterModel(
    val parentParameterTitle: String,
    val iconResource: Int, // Changed the name here
    val subParameterModel: List<SubParameterModel>,
    var isExpanded: Boolean = false
)

