package com.example.philipnews.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.Network
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.philipnews.models.Article
import com.example.philipnews.models.NewsResponse
import com.example.philipnews.repository.NewsRepository
import com.example.philipnews.utils.NewsApplication
import com.example.philipnews.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import okio.IOException
import retrofit2.Response
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis


class NewsViewModel(
     app:Application,
    val newsRepository: NewsRepository
):AndroidViewModel(app) {


    // Live Data
    private val _resourceNewsLiveData=MutableLiveData<Resource<NewsResponse>>()
    val resourceNewsLiveData:LiveData<Resource<NewsResponse>>
        get() = _resourceNewsLiveData

    // State Flow
    private val _newsStateFlow= MutableStateFlow<Resource<NewsResponse>?>(null)
     val newsStateFlow=_newsStateFlow.asStateFlow()


    var breakNewPage=1
   private var breakingNewsResponse:NewsResponse?=null


    private val _searchNewsLiveData=MutableLiveData<Resource<NewsResponse>>()
    val searchNewsLiveData:LiveData<Resource<NewsResponse>>
        get() = _searchNewsLiveData
     var searchNewsPage=1
    private var searchNewsResponse:NewsResponse?=null


    init {
    getBreakingNews("us")
}


  fun getBreakingNews(countryCode: String)=viewModelScope.launch(Dispatchers.IO) {
      //Live Data
     // safeBreakingNewsCallWithLiveData(countryCode)

      // State Flow
      safeBreakingNewsCallWithStateFlow(countryCode)
    }



    fun getSearchNews(searchQuery: String)=viewModelScope.launch(Dispatchers.IO) {
        safeSearchNewsCall(searchQuery)
    }



    private fun handleBreakingNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
       if (response.isSuccessful){
            response.body()?.let { resultResponse->
                breakNewPage++

                if (breakingNewsResponse==null){
                    breakingNewsResponse=resultResponse
                }else{
                    val oldArticles=breakingNewsResponse?.articles
                    val newArticles=resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                Log.e("codeeeeee",response.code().toString())
                return Resource.Success(breakingNewsResponse?:resultResponse)
           }
       }
       return Resource.Error(response.message())
   }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                searchNewsPage++

                if (searchNewsResponse==null){
                    searchNewsResponse=resultResponse
                }else{
                    val oldArticles=searchNewsResponse?.articles
                    val newArticles=resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                Log.e("codeeeeee",response.code().toString())
                return Resource.Success(searchNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    fun saveArticle(article:Article)=viewModelScope.launch(Dispatchers.IO) {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article:Article)=viewModelScope.launch(Dispatchers.IO) {
        newsRepository.deleteArticle(article)
    }

    fun getSavedNews()=
        newsRepository.getSavedArticles()




   private suspend fun safeSearchNewsCall(searchQuery: String){
        _searchNewsLiveData.postValue(Resource.Loading())

        try {
//          val time=  measureTimeMillis {
//          }

            if (hasInternetOrNot()){
                withTimeout(15000) {
                    val response=newsRepository.searchNews(searchQuery,searchNewsPage)
                    _searchNewsLiveData.postValue(handleSearchNewsResponse(response))
                }
            }else{
                _searchNewsLiveData.postValue(Resource.Error("No Internet Connection"))

            }

        }catch (e:TimeoutCancellationException){
            _searchNewsLiveData.postValue(Resource.Error("check Your speed Internet Connection"))
        }
        catch (t:Throwable){

            when(t){
                is IOException->_searchNewsLiveData.postValue(Resource.Error("NetWork failure"))
                else->_searchNewsLiveData.postValue(Resource.Error("conversion Error"))
            }
        }

    }


  private  suspend fun safeBreakingNewsCallWithLiveData(countryCode: String){
        _resourceNewsLiveData.postValue(Resource.Loading())

        try {
//          val time=  measureTimeMillis {
//          }

            if (hasInternetOrNot()){
                withTimeout(15000) {
                    val response=newsRepository.getBreakingNews(countryCode,breakNewPage)
                    _resourceNewsLiveData.postValue(handleBreakingNewsResponse(response))

                }
            }else{
                _resourceNewsLiveData.postValue(Resource.Error("No Internet Connection"))

            }



        }catch (e:TimeoutCancellationException){
            _resourceNewsLiveData.postValue(Resource.Error("check Your speed Internet Connection"))
        }
        catch (t:Throwable){

            when(t){
                is IOException->_resourceNewsLiveData.postValue(Resource.Error("Network failure"))
                else->_resourceNewsLiveData.postValue(Resource.Error("conversion Error"))
            }
        }

    }


    private  suspend fun safeBreakingNewsCallWithStateFlow(countryCode: String){
        _newsStateFlow.emit(Resource.Loading())

        try {
//          val time=  measureTimeMillis {
//          }

            if (hasInternetOrNot()){
                withTimeout(15000) {
                    val response=newsRepository.getBreakingNews(countryCode,breakNewPage)
                    _newsStateFlow.emit(handleBreakingNewsResponse(response))

                }
            }else{
                _newsStateFlow.emit(Resource.Error("No Internet Connection"))

            }



        }catch (e:TimeoutCancellationException){
            _newsStateFlow.emit(Resource.Error("check Your speed Internet Connection"))
        }
        catch (t:Throwable){

            when(t){
                is IOException->_newsStateFlow.emit(Resource.Error("Network failure"))
                else->_newsStateFlow.emit(Resource.Error("conversion Error"))
            }
        }

    }




    fun hasInternetOrNot():Boolean{
        val connectManager=getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1){

            val activeNetwork=connectManager.activeNetwork ?: return false
            val capabilities=connectManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when{
                capabilities.hasTransport(TRANSPORT_WIFI)->true
                capabilities.hasTransport(TRANSPORT_CELLULAR)->true
                capabilities.hasTransport(TRANSPORT_ETHERNET)->true
                else->false
            }

        }else{
            connectManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI->true
                    TYPE_MOBILE->true
                    TYPE_ETHERNET->true
                    else->false
                }
            }

            return false

        }

    }

}