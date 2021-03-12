package fieldleas.app.ui.main.profile.requests
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_requests.*
import kotlinx.android.synthetic.main.fragment_requests.view.*
import kotlinx.android.synthetic.main.requests_deny_alert.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.NewRequestsAdapter
import fieldleas.app.models.blacklist.BlackListCreate
import fieldleas.app.models.blacklist.BlackListResponse
import fieldleas.app.models.booking.BookingsResponseItem
import fieldleas.app.models.booking.FieldBookingResponse
import fieldleas.app.models.booking.requests.RequestStatus
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager

class NewRequestsFragment : Fragment(), NewRequestsAdapter.OnItemClickListener {
    private val TAG = "NewRequestsFragment"
    lateinit var newRequestsAdapter: NewRequestsAdapter
    lateinit var  navController: NavController
    lateinit var sessionManager: SessionManager
    lateinit var token: String
    lateinit var retrofitInstance: ApiService
    var requests: ArrayList<BookingsResponseItem> = ArrayList<BookingsResponseItem>()
    lateinit var progressBar : ProgressBar

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_requests, container, false)
        progressBar = rootView.new_requests_fragment_progress_bar
        progressBar.visibility = View.VISIBLE
        sessionManager = SessionManager(requireContext())
        token = sessionManager.fetchAuthToken().toString()
        retrofitInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        getNewRequests()

        rootView.new_requests_swipe_refresher.setColorSchemeColors(Color.parseColor("#0D8549"))
        rootView.new_requests_swipe_refresher.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                getNewRequests()

            }, 200)
        }

        return rootView

    }
    private fun getNewRequests() {
        val call = retrofitInstance.getRequests("Bearer $token", 1)
        call.enqueue(object: Callback<ArrayList<BookingsResponseItem>>{
            override fun onFailure(call: Call<ArrayList<BookingsResponseItem>>, t: Throwable?) {
                Log.e(TAG, "error : ${t?.message.toString()}")
            }
            override fun onResponse(
                call: Call<ArrayList<BookingsResponseItem>>,
                response: Response<ArrayList<BookingsResponseItem>>
            ) {  progressBar.visibility = View.GONE
                new_requests_swipe_refresher.isRefreshing = false
                    if (response.isSuccessful && response.body()!!.size != 0) {
                        requests_fragment_nothing_found.visibility = View.GONE
                        requests.clear()

                        // Old and unconfirmed requests are not shown
                        for(el in response.body()!!.indices) {
                            if (!response.body()!![el].isFinished!!) {
                                requests.add(response.body()!![el])
                            }
                        }
                        if(requests.size == 0){
                            requests_fragment_nothing_found.visibility = View.VISIBLE
                        }
                        //requests = response.body()!!

                        updateUI()
                    }

                    else{
                        requests_fragment_nothing_found.visibility = View.VISIBLE
                    }
            }
            })

        }

    private fun updateUI() {
        newRequestsAdapter = NewRequestsAdapter(requireContext(), this, requests)
        requests_new_recycler_view.adapter = newRequestsAdapter
    }
    private fun showDialog(position: Int, id: Int, bookingItem: BookingsResponseItem) {
        val alertDialog = LayoutInflater.from(context).inflate(R.layout.requests_deny_alert, null)
        val builder = AlertDialog.Builder(context)
            .setView(alertDialog)
            .show()

        val checkBox1 = alertDialog.button_add_to_black_list
        val checkBox2 = alertDialog.button_delete_request
        val checkBox3 = alertDialog.button_delete_all_requests

        checkBox1.setOnCheckedChangeListener { compoundButton, b ->
            if (checkBox1.isChecked){
                checkBox2.isChecked = false
                checkBox3.isChecked = false
            }
        }
        checkBox2.setOnCheckedChangeListener { compoundButton, b ->
            if (checkBox2.isChecked){
                checkBox3.isChecked = false
                checkBox1.isChecked = false
            }
        }
        checkBox3.setOnCheckedChangeListener { compoundButton, b ->
            if (checkBox3.isChecked){
                checkBox1.isChecked = false
                checkBox2.isChecked = false
            }
        }

        alertDialog.deny_deny.setOnClickListener {
            when {
                checkBox1.isChecked -> {
                    Log.e("user id: ", bookingItem.user!!.id.toString())
                    retrofitInstance.addToBlackList("Bearer $token", BlackListCreate(bookingItem.field!!.id!!, bookingItem.user.id!!))
                        .enqueue(object: Callback<BlackListResponse>{
                            override fun onFailure(call: Call<BlackListResponse>, t: Throwable) {
                                Log.e(TAG, t.message.toString())
                            }

                            override fun onResponse(call: Call<BlackListResponse>, response: Response<BlackListResponse>) {
                                if (response.isSuccessful){
                                    deleteRequest("Bearer $token", id, bookingItem, position)
                                    Toast.makeText(context, "Пользователь добавлен в черный список", Toast.LENGTH_SHORT).show()
                                }
                            }

                        })
                }
                checkBox2.isChecked -> {
                    Log.e("user id: ", id.toString())
                    deleteRequest("Bearer $token", id, bookingItem, position)
                }
                checkBox3.isChecked -> {
                    Log.e("user id: ", id.toString())
                    retrofitInstance.deleteAllRequestsFromUser("Bearer $token", bookingItem.user!!.id!!).enqueue(object: Callback<Void>{
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e(TAG, t.message.toString())
                        }

                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful){
                                Toast.makeText(context, "все запросы удалены", Toast.LENGTH_SHORT).show()
                                requests.remove(bookingItem)
                                newRequestsAdapter.notifyItemChanged(position)
                            } else{
                                Toast.makeText(context, "Произошла ошибка, попробуйте еще раз", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
            builder.dismiss()
        }
        alertDialog.deny_cancel.setOnClickListener {
            builder.dismiss()
            requireActivity().onBackPressed()
        }
        }

    private fun deleteRequest(s: String, id: Int, bookingItem: BookingsResponseItem, position: Int) {
        retrofitInstance.deleteRequest("Bearer $token", id).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, t.message.toString())
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    Toast.makeText(context, "Запрос удален", Toast.LENGTH_SHORT).show()
                    requests.remove(bookingItem)
                    newRequestsAdapter.notifyItemRemoved(position)
                } else{
                    Toast.makeText(context, "Произошла ошибка, попробуйте еще раз", Toast.LENGTH_SHORT).show()
                }
            }

        })

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onItemAccept(position: Int, id: Int, bookingItem : BookingsResponseItem) {
        retrofitInstance.request("Bearer $token", id, RequestStatus(bookingItem.field!!.id!!, 2))
                .enqueue(object: Callback<FieldBookingResponse>{
                    override fun onFailure(call: Call<FieldBookingResponse>, t: Throwable) {
                        Toast.makeText(context, "Проверьте интернет соединение", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<FieldBookingResponse>, response: Response<FieldBookingResponse>) {
                        if (response.isSuccessful){
                            requests.remove(bookingItem)
                            newRequestsAdapter.notifyItemRemoved(position)
                            Toast.makeText(context, "Запрос подтвержден", Toast.LENGTH_SHORT).show()
                        }
                    }

                })
    }

    override fun onItemDeny(position: Int, id: Int, bookingItem: BookingsResponseItem) {
        showDialog(position, id, bookingItem)
    }
}