package fieldleas.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import fieldleas.app.models.news.NewsListResponseItem
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance


class RetroViewModel : ViewModel() {

    var recyclerListData: MutableLiveData<MutableList<NewsListResponseItem>> = MutableLiveData()

    fun getRecyclerListDataObserver(): MutableLiveData<MutableList<NewsListResponseItem>> {
        return recyclerListData
    }

    fun makeApiCallForNewsList() {
        val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        val call = retroInstance.getNewsList()

        call.enqueue(object : Callback<MutableList<NewsListResponseItem>>{
            override fun onResponse(call: Call<MutableList<NewsListResponseItem>>, response: Response<MutableList<NewsListResponseItem>>) {
                if(response.isSuccessful) {
                    //recyclerViewAdapter.setListData(response.body()?.items!!)
                    //recyclerViewAdapter.notifyDataSetChanged()
                    recyclerListData.postValue(response.body())
                } else {
                    recyclerListData.postValue(null)
                }
            }

            override fun onFailure(call: Call<MutableList<NewsListResponseItem>>, t: Throwable) {
                // Toast.makeText(this@RecyclerViewActivity, "Error in getting data from api.", Toast.LENGTH_LONG).show()

                recyclerListData.postValue(null)
            }
        })
    }
}