package fieldleas.app.ui.main.profile.requests

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_requests_closed.view.*
import kotlinx.android.synthetic.main.fragment_requests_confirmed.requests_fragment_nothing_found
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.ClosedRequestsAdapter
import fieldleas.app.models.booking.BookingsResponseItem
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager

class ClosedRequestsFragment : Fragment() {

    private val TAG = "ClosedRequestsFragment"
    private lateinit var adapter : ClosedRequestsAdapter
    lateinit var sessionManager: SessionManager
    lateinit var token: String
    var closedRequests : ArrayList<BookingsResponseItem> = ArrayList<BookingsResponseItem>()
    lateinit var retrofitInstance: ApiService
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_requests_closed, container, false)
        progressBar = rootView.closed_requests_fragment_progress_bar
        progressBar.visibility = View.VISIBLE
        sessionManager = SessionManager(requireContext())
        token = sessionManager.fetchAuthToken().toString()
        retrofitInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        getRequests(rootView)

        rootView.closed_requests_swipe_refresher.setColorSchemeColors(Color.parseColor("#0D8549"))
        rootView.closed_requests_swipe_refresher.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                getRequests(rootView)

            }, 200)
        }

        return rootView

    }

    private fun getRequests(view: View) {
        retrofitInstance.getRequests("Bearer $token", 3)
            .enqueue(object: Callback<ArrayList<BookingsResponseItem>>{
                override fun onFailure(call: Call<ArrayList<BookingsResponseItem>>, t: Throwable) {
                    Log.e("closed", t.message.toString())
                }
                override fun onResponse(
                    call: Call<ArrayList<BookingsResponseItem>>,
                    response: Response<ArrayList<BookingsResponseItem>>
                ) {
                    progressBar.visibility = View.GONE
                    view.closed_requests_swipe_refresher.isRefreshing = false
                    if (response.isSuccessful && response.body()!!.size != 0){
                        requests_fragment_nothing_found.visibility = View.GONE
                        closedRequests.clear()
                        closedRequests = response.body()!!
                        Log.e("repsonse", closedRequests.size.toString())
                        updateUI(view)
                    }
                    else{
                        requests_fragment_nothing_found.visibility = View.VISIBLE
                    }
                }

            })
    }

    private fun updateUI(view :View) {
        adapter = ClosedRequestsAdapter(requireContext(), closedRequests)
        view.requests_closed_recycler_view.adapter = adapter
    }

}