package com.example.philipnews.models

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int,
    val message: String?=null
)