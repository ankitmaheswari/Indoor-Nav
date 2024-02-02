package com.indoornav.model

import androidx.annotation.DrawableRes

data class CategoryTabData(
    val id : Int,
    val categoryName: String,
    @DrawableRes val icon : Int,
)
