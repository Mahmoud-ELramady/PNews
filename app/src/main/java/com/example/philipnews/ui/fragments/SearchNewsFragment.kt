package com.example.philipnews.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.philipnews.R
import com.example.philipnews.adapters.NewsAdapter
import com.example.philipnews.databinding.FragmentSearchNewsBinding
import com.example.philipnews.ui.MainActivity
import com.example.philipnews.utils.Constants
import com.example.philipnews.utils.Constants.SEARCH_NEWS_TIME_DELAY
import com.example.philipnews.utils.Resource
import com.example.philipnews.viewmodel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment() {

    lateinit var viewModel: NewsViewModel

     var binding:FragmentSearchNewsBinding?=null
    private val newsAdapter: NewsAdapter by lazy { NewsAdapter(requireActivity()) }

    var job: Job?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentSearchNewsBinding.inflate(inflater, container, false)


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel=(requireActivity() as MainActivity).newsViewModel

        initRecyclerView()


        newsAdapter.setOnItemClickListner {

            val action=SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment(it)

            findNavController().navigate(action)


//            val bundle=Bundle().apply {
//                putSerializable("article",it)
//            }
//
//            findNavController().navigate(
//                R.id.action_searchNewsFragment_to_articleFragment,
//                bundle
//            )
        }



        binding?.etSearch?.addTextChangedListener{editable->
            job?.cancel()
            job= MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                if (editable.toString().isNotEmpty()){
                    viewModel.getSearchNews(editable.toString())
                }
            }
        }


        viewModel.searchNewsLiveData.observe(viewLifecycleOwner, Observer {resource->
            Log.e("liveeeeee","Nowwwww")
            when(resource){
                is Resource.Loading -> showProgressLoading()

                is Resource.Success-> {
                    hideProgressDialog()
                    resource.data?.let {response->
                        newsAdapter.listArticles= response.articles.toList()
                        val totalPage=response.totalResults/ Constants.QUERY_PAGE_SIZE +2
                        isLastPage=viewModel.searchNewsPage==totalPage
                        if (isLastPage){
                        binding?.rvSearchNews?.setPadding(0,0,0,0)
                        }
                    }

                }

                is Resource.Error->{
                    hideProgressDialog()
                    resource.message?.let {
                        Toast.makeText(requireContext(),it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

    }



    private fun hideProgressDialog() {
        binding?.paginationProgressBar?.visibility=View.GONE
        isLoading=false
    }

    private fun showProgressLoading() {
        binding?.paginationProgressBar?.visibility=View.VISIBLE
        isLoading=true


    }


    var isLoading=false

    var isLastPage=false
    var isScrolling=false

    val scrollListner=object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling=true
            }

        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager=recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition=layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount=layoutManager.childCount
            val totalItemCount=layoutManager.itemCount

            val isNotLoadingAndNotLastPage=!isLoading&&!isLastPage
            val isAtLastItem=firstVisibleItemPosition+visibleItemCount >= totalItemCount
            val isNotAtBeginning=firstVisibleItemPosition>=0
            val isTotalMoreThanVisible=totalItemCount>= Constants.QUERY_PAGE_SIZE
            val shouldPaginate=isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    &&isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                viewModel.getSearchNews(binding?.etSearch?.text.toString())
                isScrolling=false
            }


        }
    }



    fun initRecyclerView(){
        binding?.rvSearchNews?.apply {
            layoutManager= LinearLayoutManager(requireActivity())
            adapter=newsAdapter
            addOnScrollListener(this@SearchNewsFragment.scrollListner)

        }
    }



}