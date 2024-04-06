package com.example.philipnews.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

import com.example.philipnews.R
import com.example.philipnews.databinding.FragmentArticleBinding
import com.example.philipnews.models.Article
import com.example.philipnews.ui.MainActivity
import com.example.philipnews.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class ArticleFragment : Fragment() {

    val args:ArticleFragmentArgs? by navArgs()

lateinit var viewModel:NewsViewModel

    private var _binding:FragmentArticleBinding?=null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding=FragmentArticleBinding.inflate(inflater,container,false)

        val article=args?.article
        viewModel=(requireActivity() as MainActivity).newsViewModel


        try {

            binding?.webViewArticle?.apply {
                webViewClient= WebViewClient()
                article?.url?.let { loadUrl(it) }
            }


        }catch (e:Exception){
            Log.e("articleError",e.message.toString())
        }


        binding?.fab?.setOnClickListener {
            binding?.fab?.setImageResource(R.drawable.fill_favorite)
            article?.let {
                viewModel.saveArticle(it)
            }
            Snackbar.make(binding!!.root,"article is saved",Snackbar.LENGTH_SHORT).show()

        }


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}