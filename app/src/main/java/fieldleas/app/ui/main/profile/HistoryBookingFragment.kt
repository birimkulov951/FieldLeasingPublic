package fieldleas.app.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_history_bookings.*
import kotlinx.android.synthetic.main.fragment_history_bookings.view.*
import retrofit2.Call
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.BookItemAdapter
import fieldleas.app.models.booking.BookingsResponseItem
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager

class HistoryBookingFragment : Fragment(), BookItemAdapter.OnItemClickListener {

    private val TAG = "HistoryBookingFragment"

    private var bookingItems: ArrayList<BookingsResponseItem> = ArrayList()
    lateinit var historyAdapter : BookItemAdapter
    lateinit var navController : NavController
    lateinit var sessionManager: SessionManager
    lateinit var token: String
    lateinit var retrofitInstance: ApiService

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_history_bookings, container, false)

        sessionManager = SessionManager(requireContext())
        token = sessionManager.fetchAuthToken().toString()
        retrofitInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        rootView.history_booking_back_button.setOnClickListener {
            activity?.onBackPressed()
        }


        getBookings(rootView)

        return rootView
    }


    private fun getBookings(rootView: View) {

        val call = token.let { retrofitInstance.getBookings("Bearer $it") }

        call.enqueue(object : retrofit2.Callback<ArrayList<BookingsResponseItem>> {
            override fun onFailure(call: Call<ArrayList<BookingsResponseItem>>, t: Throwable) {
                Log.e("error", " getting bookings ${t.message}")
                Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                    call: Call<ArrayList<BookingsResponseItem>>,
                    response: Response<ArrayList<BookingsResponseItem>>
            ) {
                if (response.isSuccessful) {

                    if (response.body()!!.isEmpty()) {
                        history_empty_bookings_text_view.visibility = View.VISIBLE
                    } else {
                        bookingItems.clear()
                        for (i in 0 until response.body()!!.size) {
                            if (response.body()!![i].isFeedbackGiven!! && response.body()!![i].status == "Одобрено") {
                                bookingItems.add(response.body()!![i])
                            }
                        }

                        updateUI(rootView,bookingItems)
                    }


                } else {
                    Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateUI(view: View, bookingItems: List<BookingsResponseItem>) {
        historyAdapter = context?.let { BookItemAdapter(it,this, bookingItems) }!!
        historyAdapter.hideButton()
        view.history_recycler_view.adapter = historyAdapter
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onItemClick(position: Int, v: View?) {
        Log.d(TAG, "onItemClick: ")
    }

    override fun onItemCancel(position: Int, id: Int, bookingItem: BookingsResponseItem, v: View) {
        Log.d(TAG, "onItemCancel: ")
    }
}