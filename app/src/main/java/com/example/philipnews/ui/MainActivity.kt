package com.example.philipnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.philipnews.R
import com.example.philipnews.databinding.ActivityMainBinding
import com.example.philipnews.db.ArticleDatabase
import com.example.philipnews.repository.NewsRepository
import com.example.philipnews.viewmodel.NewsViewModel
import com.example.philipnews.viewmodel.NewsViewModelFactory

class MainActivity : AppCompatActivity() {
  private  lateinit var binding: ActivityMainBinding
  lateinit var repository: NewsRepository
//   var repository: NewsRepository=NewsRepository(ArticleDatabase(this))
//    val newsViewModel:NewsViewModel  by viewModels(){NewsViewModelFactory(repository)}
    lateinit var newsViewModel: NewsViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository=NewsRepository(ArticleDatabase(this))
        val viewModel:NewsViewModel  by viewModels(){NewsViewModelFactory(application,repository)}
        this.newsViewModel=viewModel

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       binding.btnMain.setupWithNavController(findNavController(R.id.fragment_nav_host))



    }

    override fun onStart() {
        super.onStart()
        Log.e("Opening","start Activity")

    }
}