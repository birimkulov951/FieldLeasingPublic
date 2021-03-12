package fieldleas.app.ui.main

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.fragment_news.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.NewsAdapter
import fieldleas.app.models.news.NewsListResponseItem
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import java.io.Serializable


class NewsFragment : Fragment(), NewsAdapter.OnItemClickListener  {

    private lateinit var navController: NavController
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View =  inflater.inflate(R.layout.fragment_news, container, false)

        rootView.news_fragment_progressbar.visibility = View.VISIBLE

        getNewsList(rootView)

        /** SwipeRefresherLayout listener*/
        rootView.news_fragment_swipe_refresher.setColorSchemeColors(Color.parseColor("#0D8549"))
        rootView.news_fragment_swipe_refresher.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                getNewsList(rootView)
            }, 500)
        }

        return rootView

    }

    private fun getNewsList(rootView: View) {

        val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        val call = retroInstance.getNewsList()

        call.enqueue(object : Callback<MutableList<NewsListResponseItem>> {

            override fun onResponse(call: Call<MutableList<NewsListResponseItem>>, response: Response<MutableList<NewsListResponseItem>>) {
                if (response.isSuccessful) {

                    if (response.body()!!.isEmpty()) {
                        news_fragment_progressbar.visibility = View.GONE
                        news_fragment_text_nothing_found.visibility = View.VISIBLE
                    } else {
                        newsAdapter = NewsAdapter(this@NewsFragment, requireContext())
                        newsAdapter.setNewsList(response.body() as MutableList<NewsListResponseItem>)

                        // Handling RecycleView and it's animation
                        val lac: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down)

                        rootView.news_fragment_recycler_view.adapter = newsAdapter

                        rootView.news_fragment_recycler_view.layoutAnimation = lac
                        rootView.news_fragment_recycler_view.startLayoutAnimation()

                        rootView.news_fragment_swipe_refresher.isRefreshing = false
                        rootView.news_fragment_progressbar.visibility = View.GONE
                    }

                }

            }

            override fun onFailure(call: Call<MutableList<NewsListResponseItem>>, t: Throwable) {
                if (!isNetworkAvailable(context)) {
                    rootView.news_fragment_progressbar.visibility = View.GONE
                    rootView.news_fragment_text_nothing_found.visibility = View.VISIBLE
                    rootView.news_fragment_text_nothing_found.text = getString(R.string.no_internet)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                }
            }

        })

    }

    override fun onItemClick(position: Int, v: View?) {

        val clickedListItem: NewsListResponseItem = newsAdapter.getProductAt(position)

        val dataToSend = Bundle()

        dataToSend.putString("NEWS_TITLE_DATA",clickedListItem.title)
        dataToSend.putString("NEWS_DESCRIPTION_DATA",clickedListItem.body)
        dataToSend.putSerializable("NEWS_IMAGE_DATA",clickedListItem.newsImages as Serializable)
        dataToSend.putString("NEWS_DATE_DATA",clickedListItem.date)
        dataToSend.putString("NEWS_TIME_DATA",clickedListItem.time)

        navController.navigate(R.id.action_newsFragment_to_newsDetailsFragment, dataToSend)

    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

}