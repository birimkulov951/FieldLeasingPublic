package fieldleas.app.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_filter_settings.*
import kotlinx.android.synthetic.main.fragment_filter_settings.view.*
import fieldleas.app.MainActivity
import com.example.fieldleasingpublic.R
import fieldleas.app.network.SessionManager
import fieldleas.app.utils.Constants.FILTER_BY_AVERAGE_RATING
import fieldleas.app.utils.Constants.FILTER_BY_BOOKINGS
import org.jetbrains.anko.support.v4.toast
import java.text.SimpleDateFormat
import java.util.*


class FilterSettingsFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val TAG = "FilterSettingsFragment"

    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager

    /**Helpers*/
    //private var isSettingsInitialized = false
    private var fieldType: String = "null"
    private var priceFrom: Int = -1
    private var priceUntil: Int = -1
    private var playersFrom: Int = -1
    private var playersUntil: Int = -1
    private var desiredDate: String = "null"
    private var desiredTimeFrom: String = "null"
    private var desiredTimeUntil: String = "null"
    private var ratingFilter: String = FILTER_BY_BOOKINGS
    private var hasParking = false
    private var isIndoor = false
    private var hasShowers = false
    private var hasLockerRooms = false
    private var hasLights = false
    private var hasRostrum = false
    private var hasEquipment = false
    private var filterHelper = 0

    var priceStartHelper = 0
    var priceEndHelper= 0
    var minPlayersHelper = 0
    var maxPlayersHelper= 0

    /**Lists*/
    private val desiredTimeArray = arrayOf("00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View =  inflater.inflate(R.layout.fragment_filter_settings, container, false)

        sessionManager = SessionManager(requireContext())

        priceStartHelper = sessionManager.fetchStartPrice()
        priceEndHelper= sessionManager.fetchEndPrice()
        minPlayersHelper = sessionManager.fetchMinPlayers()
        maxPlayersHelper= sessionManager.fetchMaxPlayers()


        observeHelper(rootView)


        /**Spinner type*/
        rootView.filter_settings_fragment_field_type.setOnClickListener{
            // setup the alert builder
            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.field_type))

            builder.setSingleChoiceItems((activity as MainActivity).fieldTypeNamesList.toTypedArray(),-1) { dialogInterface, i ->
                //set text
                fieldType =  "${(i + 1)}"
                rootView.filter_settings_fragment_field_type_text.text = (activity as MainActivity).fieldTypeNamesList.toTypedArray()[i]
                rootView.filter_settings_fragment_field_type_text.setTextColor(Color.parseColor("#000000"))
                rootView.filter_settings_fragment_field_type_image.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.filter_settings_fragment_field_type_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24_black)
                // dismiss dialog
                Handler(Looper.getMainLooper()).postDelayed({
                    dialogInterface.dismiss()
                }, 600)

            }

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()

        }

        /**Price EditText listeners*/
        rootView.filter_settings_fragment_start_price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    priceFrom = s.toString().toInt()
                    rootView.filter_settings_fragment_start_price.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    priceFrom = 0
                    rootView.filter_settings_fragment_start_price.setBackgroundResource(R.drawable.background_stroke_grey)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        rootView.filter_settings_fragment_end_price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    priceUntil = s.toString().toInt()
                    rootView.filter_settings_fragment_end_price.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    priceUntil = -1
                    rootView.filter_settings_fragment_end_price.setBackgroundResource(R.drawable.background_stroke_grey)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        /**Player EditText listeners*/
        rootView.filter_settings_fragment_min_player_quantity.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    playersFrom = s.toString().toInt()
                    rootView.filter_settings_fragment_min_player_quantity.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    playersFrom = 0
                    rootView.filter_settings_fragment_min_player_quantity.setBackgroundResource(R.drawable.background_stroke_grey)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        rootView.filter_settings_fragment_max_player_quantity.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    playersUntil = s.toString().toInt()
                    rootView.filter_settings_fragment_max_player_quantity.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    playersUntil = -1
                    rootView.filter_settings_fragment_max_player_quantity.setBackgroundResource(R.drawable.background_stroke_grey)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        /**Desired time views*/
        rootView.filter_settings_fragment_desired_time_from.setOnClickListener{
            // setup the alert builder
            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.desired_time_from))

            builder.setSingleChoiceItems(desiredTimeArray,-1) { dialogInteface, i ->
                //set text
                desiredTimeFrom = desiredTimeArray[i]
                rootView.filter_settings_fragment_desired_time_from.text = desiredTimeFrom
                rootView.filter_settings_fragment_desired_time_from.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.filter_settings_fragment_desired_time_from.setTextColor(Color.parseColor("#000000"))
                // dismiss dialog
                Handler(Looper.getMainLooper()).postDelayed({
                    dialogInteface.dismiss()
                }, 600)
            }
            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
        rootView.filter_settings_fragment_desired_time_until.setOnClickListener{
            // setup the alert builder
            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.desired_time_until))

            builder.setSingleChoiceItems(desiredTimeArray,-1) { dialogInteface, i ->
                //set text
                desiredTimeUntil = desiredTimeArray[i]
                rootView.filter_settings_fragment_desired_time_until.text = desiredTimeUntil
                rootView.filter_settings_fragment_desired_time_until.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.filter_settings_fragment_desired_time_until.setTextColor(Color.parseColor("#000000"))
                // dismiss dialog
                Handler(Looper.getMainLooper()).postDelayed({
                    dialogInteface.dismiss()
                }, 600)
            }
            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }

        /**Custom date spinner*/
        rootView.filter_settings_fragment_desired_date.setOnClickListener{
            hideKeyboard(rootView)
            showDatePickerDialog()
        }

        /**Rating Radio group*/
        rootView.filter_settings_fragment_rating_radio_group.setOnCheckedChangeListener{ _, checkedId ->

            if(checkedId == R.id.filter_settings_fragment_most_popular_radio) {
                ratingFilter = FILTER_BY_BOOKINGS
                hideKeyboard(rootView)

            } else {
                ratingFilter = FILTER_BY_AVERAGE_RATING
                hideKeyboard(rootView)

            }

        }

        /**Parking check box*/
        rootView.filter_settings_fragment_parking_check_box.setOnClickListener{
            hideKeyboard(rootView)
            hasParking = filter_settings_fragment_parking_check_box.isChecked

        }

        /**Indoor Radio group*/
        rootView.filter_settings_fragment_indoor_radio_group.setOnCheckedChangeListener{ _, checkedId ->

            if(checkedId == R.id.filter_settings_fragment_is_indoor_yes) {
                isIndoor = true
                hideKeyboard(rootView)

            } else  {
                isIndoor = false
                hideKeyboard(rootView)

            }

        }


        /**Showers check box*/
        rootView.filter_settings_fragment_showers_check_box.setOnClickListener{
            hideKeyboard(rootView)
            hasShowers = filter_settings_fragment_showers_check_box.isChecked
        }

        /**Locker rooms check box*/
        rootView.filter_settings_fragment_locker_rooms_has_box.setOnClickListener{
            hideKeyboard(rootView)
            hasLockerRooms = filter_settings_fragment_locker_rooms_has_box.isChecked
        }

        /**Lights check box*/
        rootView.filter_settings_fragment_lights_check_box.setOnClickListener{
            hideKeyboard(rootView)
            hasLights = filter_settings_fragment_lights_check_box.isChecked
        }

        /**rRostrum check box*/
        rootView.filter_settings_fragment_rostrum_check_box.setOnClickListener{
            hideKeyboard(rootView)
            hasRostrum = filter_settings_fragment_rostrum_check_box.isChecked
        }

        /**rRostrum check box*/
        rootView.filter_settings_fragment_equipment_check_box.setOnClickListener{
            hideKeyboard(rootView)
            hasEquipment = filter_settings_fragment_equipment_check_box.isChecked
        }


        /**Buttons*/
        /**Saving settings*/
        rootView.filter_settings_fragment_save_button.setOnClickListener{
            hideKeyboard(rootView)

            if(desiredDate != "null" && (desiredTimeFrom == "null" || desiredTimeUntil == "null")) {

                Toast.makeText(context, getString(R.string.message_time), Toast.LENGTH_SHORT).show()

            } else if(desiredDate == "null" && (desiredTimeFrom != "null" || desiredTimeUntil != "null")) {

                Toast.makeText(context, getString(R.string.message_date), Toast.LENGTH_SHORT).show()

            }  else {

                if (priceUntil == -1) {
                    priceStartHelper = priceFrom
                    priceEndHelper = priceUntil
                } else {
                    if (priceFrom >= priceUntil){
                        priceStartHelper = priceUntil
                        priceEndHelper = priceFrom
                    } else {
                        priceStartHelper = priceFrom
                        priceEndHelper = priceUntil
                    }
                }

                if (playersUntil == -1) {
                    minPlayersHelper = playersFrom
                    maxPlayersHelper = playersUntil
                } else {
                    if (playersFrom >= playersUntil) {
                        minPlayersHelper = playersUntil
                        maxPlayersHelper = playersFrom
                    } else {
                        minPlayersHelper = playersFrom
                        maxPlayersHelper = playersUntil
                    }
                }

                if (fieldType != "null") { filterHelper += 1 }
                if (priceFrom > 0){ filterHelper += 1 }
                if (priceUntil > 0){ filterHelper += 1 }
                if (playersUntil > 0){ filterHelper += 1 }
                if (playersFrom > 0){ filterHelper += 1 }
                if (hasParking){ filterHelper += 1 }
                if (isIndoor){ filterHelper += 1 }
                if (hasShowers){ filterHelper += 1 }
                if (hasLockerRooms){ filterHelper += 1 }
                if (hasLights){ filterHelper += 1 }
                if (hasRostrum){ filterHelper += 1 }
                if (hasEquipment){ filterHelper += 1 }

                //Saving filter settings
                sessionManager.saveFilterSettings(fieldType,priceStartHelper,priceEndHelper,minPlayersHelper,maxPlayersHelper,
                    desiredDate,desiredTimeFrom,desiredTimeUntil,ratingFilter,hasParking,isIndoor,
                    hasShowers, hasLockerRooms,hasLights,hasRostrum,hasEquipment,filterHelper)

                (activity as MainActivity).onBackPressed()

            }

        }

        /**Reset button*/
        rootView.filter_settings_fragment_reset_button.setOnClickListener {

            fieldType = "null"
            priceFrom = -1
            priceUntil = -1
            playersFrom = -1
            playersUntil = -1
            desiredDate = "null"
            desiredTimeFrom = "null"
            desiredTimeUntil = "null"
            ratingFilter = FILTER_BY_BOOKINGS
            hasParking = false
            isIndoor = false
            hasShowers = false
            hasLockerRooms = false
            hasLights = false
            hasRostrum = false
            hasEquipment = false

            // Reset filter settings
            sessionManager.saveFilterSettings(fieldType,priceFrom,priceUntil,playersFrom,playersUntil,
                desiredDate,desiredTimeFrom,desiredTimeUntil,ratingFilter,hasParking,isIndoor,
                hasShowers, hasLockerRooms,hasLights,hasRostrum,hasEquipment,0)

            filter_settings_fragment_field_type_text.text = getString(R.string.field_type)
            filter_settings_fragment_field_type_text.setTextColor(Color.parseColor("#C4C4C4"))
            filter_settings_fragment_field_type_image.setBackgroundResource(R.drawable.background_stroke_grey)
            filter_settings_fragment_field_type_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            filter_settings_fragment_desired_date_text.text = getString(R.string.select_date)
            filter_settings_fragment_desired_date_text.setTextColor(Color.parseColor("#C4C4C4"))
            filter_settings_fragment_desired_date_image.setBackgroundResource(R.drawable.background_stroke_grey)
            filter_settings_fragment_desired_date_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            filter_settings_fragment_desired_time_from.text = getString(R.string.from)
            filter_settings_fragment_desired_time_from.setBackgroundResource(R.drawable.background_stroke_grey)
            filter_settings_fragment_desired_time_from.setTextColor(Color.parseColor("#C4C4C4"))
            filter_settings_fragment_desired_time_until.text = getString(R.string.till)
            filter_settings_fragment_desired_time_until.setBackgroundResource(R.drawable.background_stroke_grey)
            filter_settings_fragment_desired_time_until.setTextColor(Color.parseColor("#C4C4C4"))

            hideKeyboard(rootView)
            observeHelper(rootView)

        }

        rootView.filter_settings_fragment_back_button.setOnClickListener{
            hideKeyboard(rootView)
            (activity as MainActivity).onBackPressed()
        }

        return rootView
    }

    private fun observeHelper(rootView: View) {

        if (sessionManager.fetchFieldType() != "null") {
            fieldType = sessionManager.fetchFieldType()!!
            rootView.filter_settings_fragment_field_type_text.text = (activity as MainActivity).fieldTypeNamesList[sessionManager.fetchFieldType()!!.toInt() - 1]
            rootView.filter_settings_fragment_field_type_text.setTextColor(Color.parseColor("#000000"))
            rootView.filter_settings_fragment_field_type_image.setBackgroundResource(R.drawable.background_rectangle_black)
            rootView.filter_settings_fragment_field_type_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24_black)
        }

        if (sessionManager.fetchStartPrice() > 0) {
            priceFrom = sessionManager.fetchStartPrice()
            rootView.filter_settings_fragment_start_price.setText(sessionManager.fetchStartPrice().toString(), TextView.BufferType.EDITABLE)
            rootView.filter_settings_fragment_start_price.setBackgroundResource(R.drawable.background_rectangle_black)
        }else {
            rootView.filter_settings_fragment_start_price.setText("", TextView.BufferType.EDITABLE)
            rootView.filter_settings_fragment_start_price.setBackgroundResource(R.drawable.background_stroke_grey)
        }
        if (sessionManager.fetchEndPrice() > 0) {
            priceUntil = sessionManager.fetchEndPrice()
            rootView.filter_settings_fragment_end_price.setText(sessionManager.fetchEndPrice().toString(), TextView.BufferType.EDITABLE)
            rootView.filter_settings_fragment_end_price.setBackgroundResource(R.drawable.background_rectangle_black)
        }else {
            rootView.filter_settings_fragment_end_price.setText("", TextView.BufferType.EDITABLE)
            rootView.filter_settings_fragment_end_price.setBackgroundResource(R.drawable.background_stroke_grey)
        }
        if (sessionManager.fetchMinPlayers() != 0 && sessionManager.fetchMinPlayers() != -1) {
            playersFrom = sessionManager.fetchMinPlayers()
            rootView.filter_settings_fragment_min_player_quantity.setText(sessionManager.fetchMinPlayers().toString(), TextView.BufferType.EDITABLE)
            rootView.filter_settings_fragment_min_player_quantity.setBackgroundResource(R.drawable.background_rectangle_black)
        }else {
            rootView.filter_settings_fragment_min_player_quantity.setText("", TextView.BufferType.EDITABLE)
            rootView.filter_settings_fragment_min_player_quantity.setBackgroundResource(R.drawable.background_stroke_grey)
        }
        if (sessionManager.fetchMaxPlayers() != -1 && sessionManager.fetchMaxPlayers() != 0) {
            playersUntil = sessionManager.fetchMaxPlayers()
            rootView.filter_settings_fragment_max_player_quantity.setText(sessionManager.fetchMaxPlayers().toString(), TextView.BufferType.EDITABLE)
            rootView.filter_settings_fragment_max_player_quantity.setBackgroundResource(R.drawable.background_rectangle_black)
        }else {
            rootView.filter_settings_fragment_max_player_quantity.setText("", TextView.BufferType.EDITABLE)
            rootView.filter_settings_fragment_max_player_quantity.setBackgroundResource(R.drawable.background_stroke_grey)
        }

        if (sessionManager.fetchDesiredDate() != "null") {
            desiredDate = sessionManager.fetchDesiredDate()!!
            rootView.filter_settings_fragment_desired_date_text.text = sessionManager.fetchDesiredDate()
            rootView.filter_settings_fragment_desired_date_text.setTextColor(Color.parseColor("#000000"))
            rootView.filter_settings_fragment_desired_date_image.setBackgroundResource(R.drawable.background_rectangle_black)
            rootView.filter_settings_fragment_desired_date_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24_black)
        }
        if (sessionManager.fetchDesiredDateStart() != "null") {
            desiredTimeFrom = sessionManager.fetchDesiredDateStart()!!
            rootView.filter_settings_fragment_desired_time_from.text = sessionManager.fetchDesiredDateStart()
            rootView.filter_settings_fragment_desired_time_from.setBackgroundResource(R.drawable.background_rectangle_black)
            rootView.filter_settings_fragment_desired_time_from.setTextColor(Color.parseColor("#000000"))
        }
        if (sessionManager.fetchDesiredDateEnd() != "null") {
            desiredTimeUntil = sessionManager.fetchDesiredDateEnd()!!
            rootView.filter_settings_fragment_desired_time_until.text = sessionManager.fetchDesiredDateEnd()
            rootView.filter_settings_fragment_desired_time_until.setBackgroundResource(R.drawable.background_rectangle_black)
            rootView.filter_settings_fragment_desired_time_until.setTextColor(Color.parseColor("#000000"))
        }

        if (sessionManager.fetchRatingFilter() == FILTER_BY_BOOKINGS) {
            ratingFilter = sessionManager.fetchRatingFilter()!!
            rootView.filter_settings_fragment_most_popular_radio.isChecked = true
        } else {
            ratingFilter = sessionManager.fetchRatingFilter()!!
            rootView.filter_settings_fragment_best_feedback_radio.isChecked = true
        }


        if (sessionManager.fetchIsIndoor()) {
            isIndoor = sessionManager.fetchIsIndoor()
            rootView.filter_settings_fragment_is_indoor_yes.isChecked = true
        } else {
            isIndoor = sessionManager.fetchIsIndoor()
            rootView.filter_settings_fragment_is_indoor_no.isChecked = true
        }

        hasParking = sessionManager.fetchHasParking()
        rootView.filter_settings_fragment_parking_check_box.isChecked = sessionManager.fetchHasParking() != false

        hasShowers = sessionManager.fetchHasShowers()
        rootView.filter_settings_fragment_showers_check_box.isChecked = sessionManager.fetchHasShowers() != false

        hasLockerRooms = sessionManager.fetchHasLockerRooms()
        rootView.filter_settings_fragment_locker_rooms_has_box.isChecked = sessionManager.fetchHasLockerRooms() != false

        hasLights = sessionManager.fetchHasLights()
        rootView.filter_settings_fragment_lights_check_box.isChecked = sessionManager.fetchHasLights() != false

        hasRostrum = sessionManager.fetchHasRostrum()
        rootView.filter_settings_fragment_rostrum_check_box.isChecked = sessionManager.fetchHasRostrum() != false

        hasEquipment = sessionManager.fetchHasEquipment()
        rootView.filter_settings_fragment_equipment_check_box.isChecked = sessionManager.fetchHasEquipment() != false

    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(), /*R.style.DatePickerDialogTheme,*/
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = "$year-${month+1}-$dayOfMonth"
        val oneDayMillis = 86400000
        val parseDate = SimpleDateFormat("yyyy-MM-dd").parse(date)!!.time

        if (System.currentTimeMillis() > parseDate + oneDayMillis) {
            Toast.makeText(context, getString(R.string.date_is_wrong), Toast.LENGTH_SHORT).show()
        } else {
            desiredDate = date
            filter_settings_fragment_desired_date_text.text = desiredDate
            filter_settings_fragment_desired_date_text.setTextColor(Color.parseColor("#000000"))
            filter_settings_fragment_desired_date_image.setBackgroundResource(R.drawable.background_rectangle_black)
            filter_settings_fragment_desired_date_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24_black)
        }

    }


    private fun hideKeyboard(rootView: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

}