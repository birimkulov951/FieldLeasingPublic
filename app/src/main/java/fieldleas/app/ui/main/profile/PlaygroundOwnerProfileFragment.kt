package fieldleas.app.ui.main.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.custom_dialog_2.view.*
import kotlinx.android.synthetic.main.custom_dialog_2.view.custom_dialog_2_btn_cancel
import kotlinx.android.synthetic.main.feedback_alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_playground_owner_profile.*
import kotlinx.android.synthetic.main.fragment_playground_owner_profile.view.*
import retrofit2.Call
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.BookItemAdapter
import fieldleas.app.models.auth.RefreshToken
import fieldleas.app.models.booking.BookingsResponseItem
import fieldleas.app.models.booking.FieldBookingResponse
import fieldleas.app.models.booking.FieldReviewRequest
import fieldleas.app.models.booking.requests.RequestStatus
import fieldleas.app.models.fields.FieldReviewsItem
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager
import java.text.SimpleDateFormat

class PlaygroundOwnerProfileFragment : Fragment(), BookItemAdapter.OnItemClickListener{

    private var bookingItems: ArrayList<BookingsResponseItem> = ArrayList()
    lateinit var bookItemAdapter : BookItemAdapter
    lateinit var navController : NavController
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var sessionManager: SessionManager
    lateinit var token: String
    lateinit var retrofitInstance: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_playground_owner_profile, container, false)
        setUpDrawerMenu(rootView)
        sessionManager = SessionManager(requireContext())
        token = sessionManager.fetchAuthToken().toString()
        Log.e("token", token)
        retrofitInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        getBookings(rootView)

        rootView.playground_owner_swipe_refresher.setColorSchemeColors(Color.parseColor("#0D8549"))
        rootView.playground_owner_swipe_refresher.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                getBookings(rootView)
            }, 200)
        }


        return rootView
    }

    private fun getBookings(view: View) {
        val call = token.let { retrofitInstance.getBookings("Bearer $it") }
        call.enqueue(object : retrofit2.Callback<ArrayList<BookingsResponseItem>> {
            override fun onFailure(call: Call<ArrayList<BookingsResponseItem>>, t: Throwable) {
                Log.e("error", "getting bookings ${t.message}")
                Toast.makeText(context, "Проверьте интернет соединение", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                    call: Call<ArrayList<BookingsResponseItem>>,
                    response: Response<ArrayList<BookingsResponseItem>>
            ) = if (response.isSuccessful && response.body()!!.isNotEmpty()) {
                view.profile_empty_bookings_text_view.visibility = View.GONE
                bookingItems.clear()
                for (i in 0 until response.body()!!.size) {
                    if (response.body()!![i].status == "Одобрено" && !response.body()!![i].isFeedbackGiven!!) {
                        Log.e("TAG", "Одобрено: ${response.body()!![i]}")
                        bookingItems.add(response.body()!![i])
                    } else if (response.body()!![i].status == "Открыто" && !response.body()!![i].isFinished!!) {
                        bookingItems.add(response.body()!![i])
                        Log.e("TAG", "Открыто: ${response.body()!![i]}")
                    }
                }
                if (bookingItems.size == 0){
                    profile_empty_bookings_text_view.visibility = View.VISIBLE
                }
                playground_owner_swipe_refresher.isRefreshing = false
                updateUI(view, bookingItems)

            } else if (response.code() == 403) {
                retrofitInstance.refreshToken(RefreshToken("$token")).enqueue(object :
                        retrofit2.Callback<RefreshToken> {
                    override fun onFailure(call: Call<RefreshToken>, t: Throwable) {
                        Toast.makeText(context, "проверьте интернет соединение", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                            call: Call<RefreshToken>,
                            response: Response<RefreshToken>
                    ) {
                        if (response.isSuccessful) {
                            token = response.body()!!.token
                            sessionManager.deleteOldToken()
                            sessionManager.saveAuthToken(token)
                            Handler(Looper.getMainLooper()).postDelayed({
                                getBookings(view)
                            }, 150)
                            Log.e("refreshed", "successfully ${sessionManager.fetchAuthToken()}")
                        } else {
                            Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_SHORT).show()
                        }
                    }

                })
            } else {
                profile_empty_bookings_text_view.visibility = View.VISIBLE
            }
        })
    }
    private fun updateUI(view: View, bookingItems: List<BookingsResponseItem>) {
        bookItemAdapter = context?.let { BookItemAdapter(it, this, bookingItems) }!!
        view.playground_owner_profile_recycler_view.adapter = bookItemAdapter
    }
    //drawer menu
    private fun setUpDrawerMenu(rootView: View) {
        rootView.toolbar.setNavigationIcon(R.drawable.ic_burger_menu)
        toggle = ActionBarDrawerToggle(rootView.context as Activity?, rootView.drawer_layout, rootView.toolbar, R.string.open, R.string.close)
        rootView.drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        rootView.nav_view.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.requests -> navController.navigate(R.id.action_playgroundOwnerProfileFragment_to_requestsFragment)
                R.id.history -> navController.navigate(R.id.action_playgroundOwnerProfileFragment_to_historyBookingFragment)
                R.id.my_playgrounds -> navController.navigate(R.id.action_playgroundOwnerProfileFragment_to_myFieldsFragment)
                R.id.blacklist -> navController.navigate(R.id.action_playgroundOwnerProfileFragment_to_blackListFragment)
                R.id.personal_info -> navController.navigate(R.id.action_playgroundOwnerProfileFragment_to_personalInformationFragment)
                R.id.add_playground -> navController.navigate(R.id.action_playgroundOwnerProfileFragment_to_addFieldFragment)
                R.id.exit_owner_profile -> {
                    val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_2,null)
                    val builder = AlertDialog.Builder(context).setView(alertDialog).show()
                    alertDialog.custom_dialog_2_alert_title.text = getString(R.string.exit_really)

                    alertDialog.custom_dialog_2_btn_cancel.setOnClickListener {
                        builder.dismiss()
                    }

                    alertDialog.custom_dialog_2_btn_ok.setOnClickListener {
                        builder.dismiss()
                        sessionManager.deleteOldToken()
                        sessionManager.deleteContactInfo()
                        navController.navigate(R.id.action_playgroundOwnerProfileFragment_to_playgroundListFragment)
                    }
                }
            };true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onItemClick(position: Int, v: View?) {
        val clickedItem: BookingsResponseItem = bookItemAdapter.getItemAt(position)

        val bundle = Bundle()
        bundle.putInt("DATA_ID",clickedItem.field!!.id!!)
        bundle.putString("DATA_NAME",clickedItem.field.name)
        bundle.putBoolean("DATA_IS_EMPTY",true)

        navController.navigate(R.id.action_playgroundOwnerProfileFragment_to_viewPagerFragment,bundle)
    }

    @SuppressLint("ClickableViewAccessibility", "SimpleDateFormat")
    override fun onItemCancel(position: Int, id: Int, bookingItem: BookingsResponseItem, v: View) {

        if(bookingItem.isFinished!! && bookingItem.status == "Одобрено" && bookingItem.isFeedbackGiven == false) {
            val fieldId = bookingItem.field!!.id
            val userId = bookingItem.user!!.id
            var rating = -1.0f
            var description = ""

            // Custom Alert Dialog
            val alertDialog = LayoutInflater.from(context).inflate(R.layout.feedback_alert_dialog, null)
            val builder = AlertDialog.Builder(context).setView(alertDialog).show()
            builder.setView(v);
            alertDialog.feedback_cancel_button.setOnClickListener {
                builder.dismiss()
            }
            alertDialog.feedback_rating_bar.setOnRatingBarChangeListener{ ratingBar: RatingBar, fl: Float, b: Boolean ->
                rating = fl
                //Toast.makeText(context, "$rating", Toast.LENGTH_SHORT).show()
            }
            alertDialog.feedback_send_button.setOnClickListener {
                description = alertDialog.feedback_edit_text.text.toString()
                if (rating == -1.0f) {
                    Toast.makeText(context, getString(R.string.rate), Toast.LENGTH_SHORT).show()

                } else if (description == "") {
                    Toast.makeText(context, getString(R.string.add_description), Toast.LENGTH_SHORT).show()

                } else {
                    val body = FieldReviewRequest(description,bookingItem.id,rating,userId)
                    retrofitInstance.createFieldReview("Bearer $token", body)
                            .enqueue(object:retrofit2.Callback<FieldReviewsItem> {

                                override fun onResponse(
                                        call: Call<FieldReviewsItem>,
                                        response: Response<FieldReviewsItem>
                                ) {
                                    if (response.isSuccessful) {

                                        Toast.makeText(context, getString(R.string.description_added), Toast.LENGTH_SHORT).show()
                                        val clickedItem: BookingsResponseItem = bookItemAdapter.getItemAt(position)
                                        bookingItems.remove(clickedItem)
                                        bookItemAdapter.notifyDataSetChanged()
                                        builder.dismiss()

                                    } else{
                                        Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<FieldReviewsItem>, t: Throwable) {
                                    Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                                }
                            })
                }
            }

        } else {
            val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_2,null)
            val builder = AlertDialog.Builder(context).setView(alertDialog).show()
            alertDialog.custom_dialog_2_alert_title.text = getString(R.string.cancel_book_really)

            alertDialog.custom_dialog_2_btn_cancel.setOnClickListener {
                builder.dismiss()
            }

            alertDialog.custom_dialog_2_btn_ok.setOnClickListener {
                builder.dismiss()
                retrofitInstance.request("Bearer $token", id, RequestStatus(bookingItem.field!!.id!!, 3))
                        .enqueue(object:retrofit2.Callback<FieldBookingResponse> {
                            override fun onFailure(call: Call<FieldBookingResponse>, t: Throwable) {
                                Toast.makeText(context, "Проверьте интернет соединение", Toast.LENGTH_SHORT).show()
                            }
                            override fun onResponse(
                                    call: Call<FieldBookingResponse>,
                                    response: Response<FieldBookingResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Бронь отменена", Toast.LENGTH_SHORT).show()
                                    bookingItems.remove(bookingItem)
                                    bookItemAdapter.notifyItemRemoved(position)

                                }
                                else{
                                    Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })

            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

}
