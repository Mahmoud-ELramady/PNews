package com.example.philipnews.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.philipnews.repository.NewsRepository
import java.lang.IllegalArgumentException


@Suppress("UNCHECKED_CAST")
class NewsViewModelFactory(
    private val app: Application,
    private val newsRepository:NewsRepository
):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)){
            return NewsViewModel(app,newsRepository) as T

        }
        throw  IllegalArgumentException("view model not found")
    }
}