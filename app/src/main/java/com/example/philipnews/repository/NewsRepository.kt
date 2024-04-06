package com.example.philipnews.repository

import com.example.philipnews.api.RetrofitInstance
import com.example.philipnews.db.ArticleDatabase
import com.example.philipnews.models.Article

class NewsRepository(
    val dp:ArticleDatabase
) {

    suspend fun getBreakingNews(countryCode:String,pageNumber:Int)=
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery:String,pageNumber:Int)=
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)


    suspend fun upsert(article: Article)=dp.articleDao().upsert(article)
    suspend fun deleteArticle(article: Article)=dp.articleDao().deleteArticle(article)

    fun getSavedArticles()=dp.articleDao().getAllArticles()


}