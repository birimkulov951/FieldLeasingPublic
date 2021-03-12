package fieldleas.app.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_fields.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.FieldsAdapter
import fieldleas.app.models.fields.FieldListItem
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager
import java.io.Serializable

class FieldsFragment : Fragment(),FieldsAdapter.OnItemClickListener {

    private val TAG = "FieldsFragment"
    private lateinit var fieldsAdapter: FieldsAdapter

    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View =  inflater.inflate(R.layout.fragment_fields, container, false)
        sessionManager = SessionManager(requireContext())

        fieldsAdapter = FieldsAdapter(this@FieldsFragment, requireContext())


        rootView.fields_fragment_progressbar.visibility = View.VISIBLE

        /** Calling Fields */
        if (sessionManager.fetchStartPrice() > 0 || sessionManager.fetchEndPrice() > 0 || sessionManager.fetchMinPlayers() > 0 || sessionManager.fetchMaxPlayers() > 0 ||
            /*desiredDate != "null" || desiredTimeFrom != "null" ||  desiredTimeUntil != "null" ||*/
            sessionManager.fetchFieldType()!! != "null" || sessionManager.fetchHasParking() || sessionManager.fetchIsIndoor() ||
            sessionManager.fetchHasShowers() || sessionManager.fetchHasLockerRooms() || sessionManager.fetchHasLights() ||
            sessionManager.fetchHasRostrum() || sessionManager.fetchHasEquipment()/* || sessionManager.fetchRatingFilter() == FILTER_BY_AVERAGE_RATING*/
        ) {
            rootView.fields_fragment_filter_icon.setImageResource(R.drawable.ic_icon_filter_on)
            rootView.fields_fragment_filter_settings_quantity_text.visibility = View.VISIBLE
        } else {
            rootView.fields_fragment_filter_settings_quantity_text.visibility = View.GONE
        }

        getFieldsList(rootView)


        /** SwipeRefresherLayout onClickListener*/
        rootView.fields_fragment_swipe_refresher.setColorSchemeColors(Color.parseColor("#0D8549"))
        rootView.fields_fragment_swipe_refresher.setOnRefreshListener {

            Handler(Looper.getMainLooper()).postDelayed({
                /** Calling Fields */
                getFieldsList(rootView)

            }, 200)
        }

        /** Filter Settings onClickListener*/
        rootView.fields_fragment_filter_settings!!.setOnClickListener{

            navController.navigate(R.id.action_playgroundListFragment_to_filterSettingsFragment)
            //navController.navigate(R.id.action_playgroundListFragment_to_myFieldsFragment)

        }

        return rootView

    }


    private fun getFieldsList(rootView: View) {

        val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        val call: Call<MutableList<FieldListItem>> = retroInstance.getFieldsList(sessionManager.fetchRatingFilter()!!)

        call.enqueue(object : Callback<MutableList<FieldListItem>> {

            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<MutableList<FieldListItem>>,
                response: Response<MutableList<FieldListItem>>
            ) {

                if (response.isSuccessful) {

                    if (response.body()!!.isEmpty()) {
                        rootView.fields_fragment_progressbar.visibility = View.GONE
                        rootView.fields_fragment_text_nothing_found.visibility = View.VISIBLE
                    } else {

                        //Log.e(TAG, "onResponse: ${response.body()} - ${response.body()!!.size}", )
                        val res = response.body() as ArrayList<FieldListItem>

                        val resHelper = ArrayList<FieldListItem>()
                        resHelper.clear()
                        for (el in response.body()!!.indices) {
                            //Log.e(TAG, "onResponse: ${res[el].price.toFloat().toInt()}")
                            var toBeAddedNumber = 0

                            if (res[el].isApproved && !res[el].isHidden) {

                                if (sessionManager.fetchStartPrice() > 0) {
                                    if (res[el].price.toFloat()
                                                    .toInt() >= sessionManager.fetchStartPrice()
                                    ) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchEndPrice() > 0) {
                                    if (res[el].price.toFloat()
                                                    .toInt() <= sessionManager.fetchEndPrice()
                                    ) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchMinPlayers() > 0) {
                                    if (res[el].numberOfPlayers.toFloat()
                                                    .toInt() >= sessionManager.fetchMinPlayers()
                                    ) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchMaxPlayers() > 0) {
                                    if (res[el].numberOfPlayers.toFloat()
                                                    .toInt() <= sessionManager.fetchMaxPlayers()
                                    ) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchFieldType()!! != "null") {
                                    if (res[el].fieldType == sessionManager.fetchFieldType()!!
                                                    .toInt()
                                    ) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchHasParking()) {
                                    if (res[el].hasParking == sessionManager.fetchHasParking()) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchIsIndoor()) {
                                    if (res[el].isIndoor == sessionManager.fetchIsIndoor()) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchHasShowers()) {
                                    if (res[el].hasShowers == sessionManager.fetchHasShowers()) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchHasLockerRooms()) {
                                    if (res[el].hasLockerRooms == sessionManager.fetchHasLockerRooms()) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchHasLights()) {
                                    if (res[el].hasLights == sessionManager.fetchHasLights()) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchHasRostrum()) {
                                    if (res[el].hasRostrum == sessionManager.fetchHasRostrum()) {
                                        toBeAddedNumber += 1
                                    }
                                }
                                if (sessionManager.fetchHasEquipment()) {
                                    if (res[el].hasEquipment == sessionManager.fetchHasEquipment()) {
                                        toBeAddedNumber += 1
                                    }
                                }

                                if (toBeAddedNumber == sessionManager.fetchFilterHelperInt()) {
                                    resHelper.add(res[el])
                                } else if (sessionManager.fetchFilterHelperInt() == 0) {
                                    resHelper.add(res[el])
                                }
                                /*Log.e(
                                    TAG,
                                    "LOOOOOP: $toBeAddedNumber ${sessionManager.fetchFilterHelperInt()} ${sessionManager.fetchFieldType()}"
                                )*/
                            }

                        }

                        if (resHelper.size == 0) {
                            rootView.fields_fragment_progressbar.visibility = View.GONE
                            rootView.fields_fragment_text_nothing_found.visibility = View.VISIBLE
                        } else {

                            rootView.fields_fragment_filter_settings_quantity_text.text = "(${resHelper.size})"
                            //val lac: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down)
                            //fields_fragment_recycler_view.layoutAnimation = lac
                            rootView.fields_fragment_recycler_view.adapter = fieldsAdapter
                            fieldsAdapter.setFieldsList(resHelper as MutableList<FieldListItem>)
                            rootView.fields_fragment_recycler_view.startLayoutAnimation()

                            rootView.fields_fragment_swipe_refresher.isRefreshing = false
                            rootView.fields_fragment_progressbar.visibility = View.GONE
                        }

                    }
                }


            }

            override fun onFailure(call: Call<MutableList<FieldListItem>>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
                if (!isNetworkAvailable(context)) {
                    rootView.fields_fragment_progressbar.visibility = View.GONE
                    rootView.fields_fragment_text_nothing_found.visibility = View.VISIBLE
                    rootView.fields_fragment_text_nothing_found.text = getString(R.string.no_internet)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })

    }

    override fun onItemClick(position: Int, v: View?) {

        val clickedItem: FieldListItem = fieldsAdapter.getItemAt(position)

        val bundle = Bundle()
        bundle.putInt("DATA_ID",clickedItem.id)
        bundle.putInt("DATA_FIELD_TYPE",clickedItem.fieldType!!)
        bundle.putInt("DATA_OWNER",clickedItem.owner)
        bundle.putString("DATA_PHONE_NUMBER",clickedItem.phoneNumber)
        bundle.putString("DATA_NAME",clickedItem.name)
        bundle.putString("DATA_PRICE",clickedItem.price.substringBefore("."))
        bundle.putString("DATA_LOCATION",clickedItem.location)
        bundle.putString("DATA_DESCRIPTION",clickedItem.description)
        bundle.putBoolean("DATA_IS_APPROVED",clickedItem.isApproved)
        bundle.putInt("DATA_NUMBER_OF_PLAYERS",clickedItem.numberOfPlayers)
        bundle.putBoolean("DATA_HAS_PARKING",clickedItem.hasParking)
        bundle.putBoolean("DATA_IS_INDOOR",clickedItem.isIndoor)
        bundle.putBoolean("DATA_HAS_SHOWERS",clickedItem.hasShowers)
        bundle.putBoolean("DATA_HAS_LOCKER_ROOMS",clickedItem.hasLockerRooms)
        bundle.putBoolean("DATA_HAS_LIGHTS",clickedItem.hasLights)
        bundle.putBoolean("DATA_HAS_ROSTRUM",clickedItem.hasRostrum)
        bundle.putBoolean("DATA_HAS_EQUIPMENT",clickedItem.hasEquipment)
        bundle.putInt("DATA_MINIMUM_SIZE",clickedItem.minimumSize)
        bundle.putInt("DATA_MAXIMUM_SIZE",clickedItem.maximumSize)
        bundle.putSerializable("DATA_IMAGES",clickedItem.images as Serializable)
        bundle.putFloat("DATA_RATING",clickedItem.rating)
        bundle.putInt( "DATA_NUMBER_OF_BOOKINGS",clickedItem.numberOfBookings)
        bundle.putSerializable("DATA_WORKING_HOURS",clickedItem.workingHours as Serializable)
        bundle.putBoolean("DATA_DISABLE_BOOKING", clickedItem.disableBooking)


        navController.navigate(R.id.action_playgroundListFragment_to_viewPagerFragment,bundle)
        //navController.navigate(R.id.action_playgroundListFragment_to_addStadiumFragment, bundle)

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

   /* private fun getFilterNumber(): Int {
        var integer = 0
        if (sessionManager.fetchFieldType()!! != "null"){ integer += 1 }
        if (sessionManager.fetchStartPrice() > 0){integer += 1 }
        if (sessionManager.fetchEndPrice() > 0){integer += 1 }
        if (sessionManager.fetchMinPlayers() > -1){ integer += 1}
        if (sessionManager.fetchMaxPlayers() > -1){ integer += 1}
       *//* if (sessionManager.fetchDesiredDate()!! != "null"){integer += 1 }
        if (sessionManager.fetchDesiredDateStart()!! != "null"){ integer += 1}
        if (sessionManager.fetchDesiredDateEnd()!! != "null"){ integer += 1}*//*
       *//* if (sessionManager.fetchRatingFilter()!! != "null"){ integer += 1}*//*
        if (sessionManager.fetchHasParking()){integer += 1 }
        if (sessionManager.fetchIsIndoor()){integer += 1 }
        if (sessionManager.fetchHasShowers()){ integer += 1}
        if (sessionManager.fetchHasLockerRooms()){ integer += 1}
        if (sessionManager.fetchHasLights()){integer += 1 }
        if (sessionManager.fetchHasRostrum()){ integer += 1}
        if (sessionManager.fetchHasEquipment()){ integer += 1}
        if (integer == 0) {
            integer = -1
        }
        return integer
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

}