package fieldleas.app.ui.main.profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.blacklsit_alert.view.*
import kotlinx.android.synthetic.main.fragment_blacklist.*
import kotlinx.android.synthetic.main.fragment_blacklist.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.BlackListAdapter
import fieldleas.app.models.blacklist.BlackListResponse
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager

class BlackListFragment: Fragment(), BlackListAdapter.OnItemClickListener{
    private val TAG = "BlacklistFragment"
    lateinit var navController: NavController
    lateinit var rootView: View
    lateinit var token: String
    lateinit var sessionManager: SessionManager
    lateinit var adapter: BlackListAdapter
    lateinit var progressBar: ProgressBar
    lateinit var retrofitInstance: ApiService
    var banedUsers : ArrayList<BlackListResponse> = ArrayList()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_blacklist, container, false)
        rootView.blacklist_back_button.setOnClickListener {
            activity?.onBackPressed()
        }
        sessionManager = SessionManager(requireContext())
        token = sessionManager.fetchAuthToken().toString()
        progressBar = rootView.blacklist_progress_bar
        progressBar.visibility = View.VISIBLE
        retrofitInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        getListOfBanedUsers()

        return rootView
    }

    private fun getListOfBanedUsers() {
        val call = retrofitInstance.getBanedUsers("Bearer $token")
        call.enqueue(object: retrofit2.Callback<List<BlackListResponse>> {
            override fun onFailure(call: Call<List<BlackListResponse>>, t: Throwable) {
                Log.e(TAG, " ${t.message.toString()}")
            }

            override fun onResponse(
                    call: Call<List<BlackListResponse>>,
                    response: retrofit2.Response<List<BlackListResponse>>
            ) {
                    if (response.isSuccessful && response.body()!!.size != 0) {
                        progressBar.visibility = View.GONE
                        blacklist_empty_text_view.visibility = View.GONE
                        banedUsers = response.body() as ArrayList<BlackListResponse>
                        updateUI()
                    }
                    else{
                        blacklist_empty_text_view.visibility = View.VISIBLE
                    }
            }

        })
    }

    private fun updateUI() {
        adapter = BlackListAdapter(this, banedUsers)
        blacklist_recycler_view.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }

    override fun unblockUser(position: Int, name: String, id: Int) {
        showDialog(position, name, id)
    }

    private fun showDialog(position: Int, name: String, id: Int) {
        val alertDialog = LayoutInflater.from(context).inflate(R.layout.blacklsit_alert, null)
        alertDialog.blacklist_alert_name.text = "$name?"
        val builder = AlertDialog.Builder(context)
            .setView(alertDialog)
            .show()
        alertDialog.blacklist_alert_btn_cancel.setOnClickListener {
            builder.dismiss()
        }
        alertDialog.blacklist_alert_btn_unblock.setOnClickListener {
            val call = retrofitInstance.unblockUser("Bearer $token", id).enqueue(object:
                retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Поверьте интернет соединение" , Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    builder.dismiss()
                    banedUsers.remove(banedUsers[position])
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(context, "Пользователь разблокирован", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }
}