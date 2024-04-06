package com.example.philipnews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.philipnews.R
import com.example.philipnews.adapters.NewsAdapter
import com.example.philipnews.databinding.FragmentSavedNewsBinding
import com.example.philipnews.ui.MainActivity
import com.example.philipnews.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar


class SavedNewsFragment : Fragment() {

     var binding:FragmentSavedNewsBinding?=null

    private val newsAdapter: NewsAdapter by lazy { NewsAdapter(requireActivity()) }
    lateinit var newsViewModel: NewsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentSavedNewsBinding.inflate(inflater,container,false)


        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel=(requireActivity() as MainActivity).newsViewModel

        initRecyclerView()

        newsAdapter.setOnItemClickListner {
            val action=SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(it)

            findNavController().navigate(action)

        }

        val item=object:ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT,
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
               return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val article=newsAdapter.listArticles[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(binding!!.root,"article is deleted", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        newsViewModel.saveArticle(article)
                    }
                    show()
                }

            }

        }

        ItemTouchHelper(item).apply {
            attachToRecyclerView(binding!!.rvSavedNews)
        }

        newsViewModel.getSavedNews().observe(viewLifecycleOwner, Observer {
            newsAdapter.listArticles=it
        })

    }

    fun initRecyclerView(){
        binding?.rvSavedNews?.apply {
            layoutManager= LinearLayoutManager(requireActivity())
            adapter=newsAdapter

        }
    }

}