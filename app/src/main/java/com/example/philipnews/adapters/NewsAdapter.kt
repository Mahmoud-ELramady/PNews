package com.example.philipnews.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.philipnews.databinding.ItemArticlePreviewBinding
import com.example.philipnews.models.Article

class NewsAdapter(private val context: Context):RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {





   private val differCallback=object :DiffUtil.ItemCallback<Article>(){
      override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
         return oldItem.url==newItem.url
      }

      override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
         return oldItem==newItem
      }

   }

   private val asyncListDiffer=AsyncListDiffer(this,differCallback)

   var listArticles:List<Article>
      get()=asyncListDiffer.currentList
      set(value) = asyncListDiffer.submitList(value)

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
      val binding=ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
      return NewsViewHolder(binding)
   }

   override fun getItemCount(): Int=listArticles.size

   override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
      val article: Article =listArticles[position]
      holder.itemView.apply {
         Glide.with(this).load(article.urlToImage).into(holder.binding.ivArticleImage)
         setOnClickListener {
            onItemClickListner?.let {
             //  Log.e("adapterError",article.url.toString())

               try {
                  it(article)
                  Log.e("adapterError",article.url.toString())

               }catch (e:Exception){
                  Log.e("adapterError",e.message.toString())
               }
            }
         }
      }

      holder.bind(article)
   }

   inner class NewsViewHolder(val binding:ItemArticlePreviewBinding):RecyclerView.ViewHolder(binding.root) {
      fun bind(article: Article) {
         binding.apply {
            tvSource.text=article.source?.name
            tvTitle.text=article.title
            tvDescription.text=article.description
            tvPublishedAt.text=article.publishedAt
         }
      }
   }


   private var onItemClickListner:((Article)->Unit)?=null

   fun setOnItemClickListner(listner:(Article)->Unit){

      if (listner!=null) {
         onItemClickListner = listner

      }
   }

}