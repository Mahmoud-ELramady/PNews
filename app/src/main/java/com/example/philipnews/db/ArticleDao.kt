package com.example.philipnews.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.philipnews.models.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Upsert
    suspend fun upsert(article: Article):Long

    @Query("select * from articles")
    fun getAllArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)


}