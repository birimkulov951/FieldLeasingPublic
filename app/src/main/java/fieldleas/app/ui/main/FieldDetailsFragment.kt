package fieldleas.app.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import kotlinx.android.synthetic.main.fragment_field_details.*
import kotlinx.android.synthetic.main.fragment_field_details.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.ImageSliderAdapter
import fieldleas.app.adapters.ZoomOutPageTransformer
import fieldleas.app.models.fields.FieldListItem
import fieldleas.app.models.fields.Image
import fieldleas.app.models.fields.WorkingHour
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager
import fieldleas.app.utils.Constants.FR2
import fieldleas.app.utils.Constants.MN2
import fieldleas.app.utils.Constants.SA2
import fieldleas.app.utils.Constants.SN2
import fieldleas.app.utils.Constants.TH2
import fieldleas.app.utils.Constants.TU2
import fieldleas.app.utils.Constants.WD2
import java.io.Serializable

class FieldDetailsFragment : Fragment() {

    private lateinit var navController: NavController

    private lateinit var sessionManager: SessionManager

    private var isScheduleVisible = false
    private var isCharacteristicsVisible = false
    private var monday = ""
    private var tuesday = ""
    private var wednesday = ""
    private var thursday = ""
    private var friday = ""
    private var saturday = ""
    private var sunday = ""

    /** Main vars for binding*/
    private var fieldId = -1
    private var fieldType = -1
    private var owner = -1
    private var phoneNumber = ""
    private var name = ""
    private var price = ""
    private var location = ""
    private var description = ""
    private var isApproved = false
    private var numberOfPlayers = -1
    private var hasParking = false
    private var isIndoor = false
    private var hasShowers = false
    private var hasLockerRooms = false
    private var hasLights = false
    private var hasRostrum = false
    private var hasEquipment = false
    private var minimumSize = -1
    private var maximumSize = -1
    private var images = emptyList<Image>()
    private var rating = -1.0f
    private var numberOfBookings = -1
    private var workingHours = emptyList<WorkingHour>()
    private var disableBooking = false
    private var isBlacklisted = false


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_field_details, container, false)
        sessionManager = SessionManager(requireContext())

        fieldId = (parentFragment as ViewPagerFragment).fieldId

        if ((parentFragment as ViewPagerFragment).isDataEmpty) {
            getDataById(rootView)
        } else {

            fieldType = (parentFragment as ViewPagerFragment).fieldType
            owner = (parentFragment as ViewPagerFragment).owner
            phoneNumber = (parentFragment as ViewPagerFragment).phoneNumber
            name = (parentFragment as ViewPagerFragment).name
            price = (parentFragment as ViewPagerFragment).price
            location = (parentFragment as ViewPagerFragment).location
            description = (parentFragment as ViewPagerFragment).description
            isApproved = (parentFragment as ViewPagerFragment).isApproved
            numberOfPlayers = (parentFragment as ViewPagerFragment).numberOfPlayers
            hasParking = (parentFragment as ViewPagerFragment).hasParking
            isIndoor = (parentFragment as ViewPagerFragment).isIndoor
            hasShowers = (parentFragment as ViewPagerFragment).hasShowers
            hasLockerRooms = (parentFragment as ViewPagerFragment).hasLockerRooms
            hasLights = (parentFragment as ViewPagerFragment).hasLights
            hasRostrum = (parentFragment as ViewPagerFragment).hasRostrum
            hasEquipment = (parentFragment as ViewPagerFragment).hasEquipment
            minimumSize = (parentFragment as ViewPagerFragment).minimumSize
            maximumSize = (parentFragment as ViewPagerFragment).maximumSize
            images = (parentFragment as ViewPagerFragment).images
            rating = (parentFragment as ViewPagerFragment).rating
            numberOfBookings = (parentFragment as ViewPagerFragment).numberOfBookings
            workingHours = (parentFragment as ViewPagerFragment).workingHours
            disableBooking = (parentFragment as ViewPagerFragment).disableBooking

            bindingViews(rootView)

        }




        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun bindingViews(rootView: View) {
        val list = ArrayList<String>()
        for (element in images) {
            list.add(element.image)
        }

        /** Setting sliding image adapter*/
        val dotsIndicator = rootView.findViewById<SpringDotsIndicator>(R.id.field_details_fragment_dots_indicator)
        val viewPager2 = rootView.findViewById<ViewPager2>(R.id.field_details_fragment_view_pager_2)
        val imageSliderAdapter = ImageSliderAdapter(list,requireContext())
        viewPager2.adapter = imageSliderAdapter

        val zoomOutPageTransformer = ZoomOutPageTransformer()
        viewPager2.setPageTransformer { page, position ->
            zoomOutPageTransformer.transformPage(page, position)
        }
        dotsIndicator.setViewPager2(viewPager2)


        /** Setting Field Name*/
        rootView.field_details_fragment_field_name.text = name

        /** Setting Rating Bar*/
        rootView.field_details_fragment_rating_bar.rating = rating

        /** Setting schedule text views*/
        rootView.field_details_fragment_schedule.setOnClickListener{
            checkSchedule(rootView)
        }

        /** Setting price*/
        rootView.field_details_fragment_price_text.text =  "$price ${getString(R.string.som_currency)}"

        /** Setting location*/
        rootView.field_details_fragment_address_text.text =  location

        /** Setting number of players*/
        rootView.field_details_fragment_number_of_players_text.text = " до $numberOfPlayers"

        /** Setting number of players*/
        rootView.field_details_fragment_field_sizes.text = " $minimumSize x $maximumSize"

        /** Setting characteristics text views*/
        rootView.field_details_fragment_additional_details.setOnClickListener{

            val rotateRight = AnimationUtils.loadAnimation(activity, R.anim.rotate_right)
            val rotateLeft = AnimationUtils.loadAnimation(activity, R.anim.rotate_left)
            if (!isCharacteristicsVisible) {
                isCharacteristicsVisible = true

                TransitionManager.beginDelayedTransition(field_details_fragment_details_layout, AutoTransition())
                field_details_fragment_additional_details_arrow_icon.startAnimation(rotateRight)

                rootView.field_details_fragment_details_layout.visibility = View.VISIBLE

                if (hasParking) {
                    rootView.field_details_fragment_field_has_parking.text = ": есть"
                } else {
                    rootView.field_details_fragment_field_has_parking.text = ": нет"
                }

                if (hasParking) {
                    rootView.field_details_fragment_field_has_parking.text = ": есть"
                } else {
                    rootView.field_details_fragment_field_has_parking.text = ": нет"
                }
                if (isIndoor) {
                    rootView.field_details_fragment_field_is_indoor.text = ": есть"
                } else {
                    rootView.field_details_fragment_field_is_indoor.text = ": нет"
                }
                if (hasShowers) {
                    rootView.field_details_fragment_field_has_showers.text = ": есть"
                } else {
                    rootView.field_details_fragment_field_has_showers.text = ": нет"
                }
                if (hasLockerRooms) {
                    rootView.field_details_fragment_field_has_locker_rooms.text = ": есть"
                } else {
                    rootView.field_details_fragment_field_has_locker_rooms.text = ": нет"
                }
                if (hasLights) {
                    rootView.field_details_fragment_field_has_lights.text = ": есть"
                } else {
                    rootView.field_details_fragment_field_has_lights.text = ": нет"
                }
                if (hasRostrum) {
                    rootView.field_details_fragment_field_has_rostrum.text = ": есть"
                } else {
                    rootView.field_details_fragment_field_has_rostrum.text = ": нет"
                }
                if (hasEquipment) {
                    rootView.field_details_fragment_field_has_equipment.text = ": есть"
                } else {
                    rootView.field_details_fragment_field_has_equipment.text = ": нет"
                }

            } else {
                isCharacteristicsVisible = false
                field_details_fragment_additional_details_arrow_icon.startAnimation(rotateLeft)
                rootView.field_details_fragment_details_layout.visibility = View.GONE
            }

        }

        /** Setting phone number*/
        rootView.field_details_fragment_field_call_number.text = phoneNumber

        /** Setting phone call opportunity*/
        rootView.field_details_fragment_field_call_number.setOnClickListener{
            checkPermission(phoneNumber)
        }

        /** Setting description*/
        rootView.field_details_fragment_description_text.text = description



        /** Book button listener*/
        // getFieldById()
        if (!isApproved) {
            rootView.field_details_fragment_book_field_button.visibility = View.GONE
            rootView.distance_layout.visibility = View.GONE
        } else  if (disableBooking){
            rootView.field_details_fragment_book_field_button.visibility = View.GONE
            rootView.distance_layout.visibility = View.GONE
        }

//        if (isBlacklisted){
//            rootView.field_details_fragment_book_field_button.visibility = View.GONE
//        }
        else {
            rootView.field_details_fragment_book_field_button.setOnClickListener {
                // Sending field id to be able to make right booking in Booking Fragment.
                val bundle = Bundle()
                bundle.putInt("DATA_ID", fieldId)
                bundle.putSerializable("DATA_WORKING_HOURS", workingHours as Serializable)
                navController.navigate(R.id.action_viewPagerFragment_to_bookingFragment2, bundle)
            }
        }
    }

    private fun getDataById(rootView: View) {
        val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        val call = retroInstance.getFieldListItemById("Bearer ${sessionManager.fetchAuthToken()}", fieldId)

        call.enqueue(object: Callback<FieldListItem> {

            override fun onResponse(call: Call<FieldListItem>, response: Response<FieldListItem>) {

                Log.e("FieldDetailsFragment", "onResponse: ${response.body()} $fieldId")

                if (response.isSuccessful) {
                    fieldType = response.body()!!.fieldType!!
                    owner = response.body()!!.owner
                    phoneNumber = response.body()!!.phoneNumber
                    name =response.body()!!.name
                    price =response.body()!!.price.substringBefore(".")
                    location = response.body()!!.location
                    description = response.body()!!.description
                    isApproved = response.body()!!.isApproved
                    numberOfPlayers = response.body()!!.numberOfPlayers
                    hasParking = response.body()!!.hasParking
                    isIndoor = response.body()!!.isIndoor
                    hasShowers = response.body()!!.hasShowers
                    hasLockerRooms = response.body()!!.hasLockerRooms
                    hasLights = response.body()!!.hasLights
                    hasRostrum = response.body()!!.hasRostrum
                    hasEquipment = response.body()!!.hasEquipment
                    minimumSize = response.body()!!.minimumSize
                    maximumSize = response.body()!!.maximumSize
                    images = response.body()!!.images as List<Image>
                    rating = response.body()!!.rating
                    numberOfBookings = response.body()!!.numberOfBookings
                    workingHours = response.body()!!.workingHours as List<WorkingHour>
                    disableBooking = response.body()!!.disableBooking
                    bindingViews(rootView)
                } else{
                    Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FieldListItem>, t: Throwable) {
                Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun checkSchedule(view: View) {

        val rotateRight = AnimationUtils.loadAnimation(activity, R.anim.rotate_right)
        val rotateLeft = AnimationUtils.loadAnimation(activity, R.anim.rotate_left)

        if (!isScheduleVisible){
            TransitionManager.beginDelayedTransition(field_details_fragment_schedule_layout, AutoTransition())

            field_details_fragment_schedule_arrow_icon.startAnimation(rotateRight)
            isScheduleVisible = true

            workingHours.forEach {

                when (it.day) {
                    MN2 -> {
                        monday = it.start.substring(0,5) + " - " + it.end.substring(0,5)
                        view.field_details_fragment_text_monday.visibility = View.VISIBLE
                        view.field_details_fragment_text_monday_hours.visibility = View.VISIBLE
                        view.field_details_fragment_text_monday_hours.text = monday
                    }
                    TU2 -> {
                        tuesday = it.start.substring(0,5) + " - " + it.end.substring(0,5)
                        view.field_details_fragment_text_tuesday.visibility = View.VISIBLE
                        view.field_details_fragment_text_tuesday_hours.visibility = View.VISIBLE
                        view.field_details_fragment_text_tuesday_hours.text = tuesday
                    }
                    WD2 -> {
                        wednesday = it.start.substring(0,5) + " - " + it.end.substring(0,5)
                        view.field_details_fragment_text_wednesday.visibility = View.VISIBLE
                        view.field_details_fragment_text_wednesday_hours.visibility = View.VISIBLE
                        view.field_details_fragment_text_wednesday_hours.text = wednesday
                    }
                    TH2 -> {
                        thursday = it.start.substring(0,5) + " - " + it.end.substring(0,5)
                        view.field_details_fragment_text_thursday.visibility = View.VISIBLE
                        view.field_details_fragment_text_thursday_hours.visibility = View.VISIBLE
                        view.field_details_fragment_text_thursday_hours.text = thursday
                    }
                    FR2 -> {
                        friday = it.start.substring(0,5) + " - " + it.end.substring(0,5)
                        view.field_details_fragment_text_friday.visibility = View.VISIBLE
                        view.field_details_fragment_text_friday_hours.visibility = View.VISIBLE
                        view.field_details_fragment_text_friday_hours.text = friday
                    }
                    SA2 -> {
                        saturday = it.start.substring(0,5) + " - " + it.end.substring(0,5)
                        view.field_details_fragment_text_saturday.visibility = View.VISIBLE
                        view.field_details_fragment_text_saturday_hours.visibility = View.VISIBLE
                        view.field_details_fragment_text_saturday_hours.text = saturday
                    }
                    SN2 -> {
                        sunday = it.start.substring(0,5) + " - " + it.end.substring(0,5)
                        view.field_details_fragment_text_sunday.visibility = View.VISIBLE
                        view.field_details_fragment_text_sunday_hours.visibility = View.VISIBLE
                        view.field_details_fragment_text_sunday_hours.text = sunday
                    }
                }
            }

        } else {

            view.field_details_fragment_schedule_arrow_icon.startAnimation(rotateLeft)
            isScheduleVisible = false
            view.field_details_fragment_text_monday.visibility = View.GONE
            view.field_details_fragment_text_tuesday.visibility = View.GONE
            view.field_details_fragment_text_wednesday.visibility = View.GONE
            view.field_details_fragment_text_thursday.visibility = View.GONE
            view.field_details_fragment_text_friday.visibility = View.GONE
            view.field_details_fragment_text_saturday.visibility = View.GONE
            view.field_details_fragment_text_sunday.visibility = View.GONE
            view.field_details_fragment_text_monday_hours.visibility = View.GONE
            view.field_details_fragment_text_tuesday_hours.visibility = View.GONE
            view.field_details_fragment_text_wednesday_hours.visibility = View.GONE
            view.field_details_fragment_text_thursday_hours.visibility = View.GONE
            view.field_details_fragment_text_friday_hours.visibility = View.GONE
            view.field_details_fragment_text_saturday_hours.visibility = View.GONE
            view.field_details_fragment_text_sunday_hours.visibility = View.GONE

        }
    }


    fun checkPermission(number: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(Manifest.permission.CALL_PHONE),
                        42)
            }
        } else {
            // Permission has already been granted
            callPhone(number)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == 42) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                callPhone(phoneNumber)
            } else {
                // permission denied, boo! Disable the
                Toast.makeText(context,getString(R.string.no_permission),Toast.LENGTH_SHORT).show()
            }
            return
        }
    }

    private fun callPhone(phoneNumber: String){
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

}