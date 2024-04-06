package com.example.philipnews.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.philipnews.R
import com.example.philipnews.adapters.NewsAdapter
import com.example.philipnews.databinding.FragmentBreakingNewsBinding
import com.example.philipnews.db.ArticleDatabase
import com.example.philipnews.repository.NewsRepository
import com.example.philipnews.ui.MainActivity
import com.example.philipnews.utils.Constants.QUERY_PAGE_SIZE
import com.example.philipnews.utils.Resource
import com.example.philipnews.viewmodel.NewsViewModel
import com.example.philipnews.viewmodel.NewsViewModelFactory
import kotlinx.coroutines.launch


class BreakingNewsFragment : Fragment() {

     var binding:FragmentBreakingNewsBinding?=null
    lateinit var newsViewModel: NewsViewModel
    private val newsAdapter:NewsAdapter by lazy { NewsAdapter(requireActivity()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentBreakingNewsBinding.inflate(inflater, container, false)

        Log.e("Opening","on Create View")


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel=(requireActivity() as MainActivity).newsViewModel


        initRecyclerView()

        newsAdapter.setOnItemClickListner { article->
            val action=BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(article)

                findNavController().navigate(action)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                newsViewModel.newsStateFlow.collect { resource->
                    when(resource){
                        is Resource.Loading -> showProgressLoading()

                        is Resource.Success-> {
                            hideProgressDialog()
                            resource.data?.let {response->
                                newsAdapter.listArticles= response.articles.toList()
                                val totalPage=response.totalResults/ QUERY_PAGE_SIZE +2
                                isLastPage=newsViewModel.breakNewPage==totalPage
                                if (isLastPage){
                                    binding?.rvBreakingNews?.setPadding(0,0,0,0)
                                }
                            }

                        }

                        is Resource.Error->{
                            hideProgressDialog()
                            resource.message?.let {
                                Toast.makeText(requireActivity(),it,Toast.LENGTH_LONG).show()
                            }
                        }

                        else ->{
                        hideProgressDialog()
                        resource?.message?.let {
                            Toast.makeText(requireActivity(),it,Toast.LENGTH_LONG).show()
                        }
                    }
                    }
                }
            }
        }


//        newsViewModel.resourceNewsLiveData.observe(viewLifecycleOwner, Observer {resource->
//            when(resource){
//                  is Resource.Loading -> showProgressLoading()
//
//                  is Resource.Success-> {
//                       hideProgressDialog()
//                      resource.data?.let {response->
//                          newsAdapter.listArticles= response.articles.toList()
//                          val totalPage=response.totalResults/ QUERY_PAGE_SIZE +2
//                          isLastPage=newsViewModel.breakNewPage==totalPage
//                          if (isLastPage){
//                              binding?.rvBreakingNews?.setPadding(0,0,0,0)
//                          }
//                      }
//
//                   }
//
//                is Resource.Error->{
//                    hideProgressDialog()
//                    resource.message?.let {
//                        Toast.makeText(requireActivity(),it,Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//        })

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

    val scrollListner=object :RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
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
            val isTotalMoreThanVisible=totalItemCount>=QUERY_PAGE_SIZE
            val shouldPaginate=isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    &&isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                newsViewModel.getBreakingNews("us")
                isScrolling=false
            }else{
                binding?.rvBreakingNews?.setPadding(0,0,0,0)
            }


        }
    }


    fun initRecyclerView(){
        binding?.rvBreakingNews?.apply {
            layoutManager=LinearLayoutManager(requireActivity())
            adapter=newsAdapter
            addOnScrollListener(this@BreakingNewsFragment.scrollListner)

        }
    }




}