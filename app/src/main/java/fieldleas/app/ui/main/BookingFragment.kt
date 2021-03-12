package fieldleas.app.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.book_alert_dialog.view.*
import kotlinx.android.synthetic.main.custom_dialog_3.view.*
import kotlinx.android.synthetic.main.custom_dialog_4.view.*
import kotlinx.android.synthetic.main.custom_dialog_5.view.*
import kotlinx.android.synthetic.main.fragment_book.*
import kotlinx.android.synthetic.main.fragment_book.view.*
import okhttp3.internal.headersContentLength
import retrofit2.Call
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.models.auth.RefreshToken
import fieldleas.app.models.booking.FieldBookingCreate
import fieldleas.app.models.booking.FieldBookingResponse
import fieldleas.app.models.fields.WorkingHour
import fieldleas.app.network.ApiClient
import fieldleas.app.network.SessionManager
import kotlinx.android.synthetic.main.fragment_filter_settings.*
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private var desiredDate: String = ""
    private var desiredTimeFrom: String = ""
    private var desiredTimeUntil: String =""
    private var name = ""
    private var number = ""
    lateinit var sessionManager: SessionManager
    lateinit var navController: NavController
    var bookingIds: ArrayList<FieldBookingResponse>  = ArrayList()
    lateinit var bundle: Bundle
    var fieldId = 0
    lateinit var token: String
    var desiredTimeArray : ArrayList<String> = ArrayList()
    lateinit var wHoursArray: ArrayList<WorkingHour>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_book, container, false)
        sessionManager = SessionManager(requireContext())
        if (sessionManager.fetchAuthToken().toString() == "null"){
            showAuthDialog()
            rootView.booking_btn_book.isEnabled = false
        }
        token = sessionManager.fetchAuthToken().toString()
        bundle = this.requireArguments()
        fieldId = bundle!!.getInt("DATA_ID")
        wHoursArray = bundle!!.getSerializable("DATA_WORKING_HOURS") as ArrayList<WorkingHour>
        var startTime = wHoursArray[0].start.substring(0,2).toInt()
        var endTime = wHoursArray[0].end.substring(0,2).toInt()

         for (i in startTime..endTime){
            desiredTimeArray.add("$i:00")
        }

        rootView.booking_back_button.setOnClickListener {
            requireActivity().onBackPressed()
        }
        rootView.booking_edit_text_name.setText(sessionManager.fetchUserName())
        rootView.booking_edit_text_number.setText(sessionManager.fetchUserPhoneNumber())


        if (rootView.booking_edit_text_name.text.isNotEmpty()){
            rootView.booking_edit_text_name.setBackgroundResource(R.drawable.background_rectangle_black)
        }
        if  (rootView.booking_edit_text_number.text.isNotEmpty()){
            rootView.booking_edit_text_number.setBackgroundResource(R.drawable.background_rectangle_black)
        }

        rootView.booking_edit_text_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (rootView.booking_edit_text_name.text.isEmpty()) {
                    rootView.booking_edit_text_name.setBackgroundResource(R.drawable.background_stroke_grey)
                } else {
                    rootView.booking_edit_text_name.setBackgroundResource(R.drawable.background_rectangle_black)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        rootView.booking_edit_text_number.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (rootView.booking_edit_text_number.text.isEmpty()) {
                    rootView.booking_edit_text_number.setBackgroundResource(R.drawable.background_stroke_grey)
                }
                if (!booking_edit_text_number?.text.toString().startsWith("+996")){
                            booking_edit_text_number?.setText("+996")
                            Selection.setSelection(booking_edit_text_number.text, booking_edit_text_number.text.length)
                        }
                else {
                    rootView.booking_edit_text_number.setBackgroundResource(R.drawable.background_rectangle_black)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        rootView.booking_choose_date_card.setOnClickListener {
            chooseDate()
        }
        rootView.booking_time_from.setOnClickListener {
            desiredTimeFrom = chooseTimeFrom(rootView.booking_time_from)
            name = booking_edit_text_name.text.toString()
            number = booking_edit_text_number.text.toString()
            rootView.booking_time_from_frame.setBackgroundResource(R.drawable.background_rectangle_black)

        }
        rootView.booking_time_until.setOnClickListener {
            desiredTimeUntil = chooseTimeUntil(rootView.booking_time_until)
            name = booking_edit_text_name.text.toString()
            number = booking_edit_text_number.text.toString()
            booking_time_to_frame.setBackgroundResource(R.drawable.background_rectangle_black)

        }
        rootView.booking_btn_book.setBackgroundResource(R.drawable.background_rectangle_green)
            rootView.booking_btn_book.setOnClickListener {
                name = rootView.booking_edit_text_name.text.toString()
                number = rootView.booking_edit_text_number.text.toString()
                val isValid = inputCheck(name, number, desiredTimeUntil, desiredTimeFrom, desiredDate)
                val fieldBooking = FieldBookingCreate(fieldId, desiredDate, desiredTimeFrom, desiredTimeUntil, 1)
                if (isValid){
                    fieldBooking?.let { it1 -> book(it1) }
                }
                else{
                    Toast.makeText(rootView.context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
        }
            return rootView
        }


    private fun showAuthDialog() {
        val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_4, null)
        val builder = AlertDialog.Builder(context)
                .setView(alertDialog)
                .show()
        alertDialog.custom_dialog_4_ok_button.setOnClickListener {
            builder.dismiss()
            navController.navigate(R.id.action_bookingFragment2_to_profileNotRegisteredUsers)
        }
    }

    private fun book(fieldBookingCreate: FieldBookingCreate) {
        val call = ApiClient().getApiService().bookTheField("Bearer $token",fieldBookingCreate)

        call.enqueue(object : retrofit2.Callback<FieldBookingResponse> {
            override fun onResponse(call: Call<FieldBookingResponse>, response: Response<FieldBookingResponse>) {
                if (response.isSuccessful) {
                    showDialog()
                    response.body()?.let { bookingIds.add(it) }
                }
                else if(response.code() == 400){
                    when(response.raw().headersContentLength().toString()){
                        "66" -> Toast.makeText(requireContext(), "Указанное время уже прошло", Toast.LENGTH_SHORT).show()
                        "109" -> alreadyBookedDialog()
                        "84" -> banedUserDialog()
                        else ->
                            Toast.makeText(requireContext(), "Невозможно совершить бронь на данное время", Toast.LENGTH_SHORT).show()
                }}

                else if (response.code() == 403){
                    when(response.raw().headersContentLength().toString()){
                        "84" -> banedUserDialog()
                        "60" -> refreshToken()
                    }
                }

            }
            override fun onFailure(call: Call<FieldBookingResponse>, t: Throwable) {
                Toast.makeText(context, "Произошла ошибка при бронировании поля, попробуйте еще раз", Toast.LENGTH_SHORT).show()
                Log.e("Error", t.message.toString())
            }

        })
    }

    private fun refreshToken() {
        val refresh = ApiClient().getApiService().refreshToken(RefreshToken("$token"))
                .enqueue(object : Callback<RefreshToken> {
                    override fun onFailure(call: Call<RefreshToken>, t: Throwable) {
                    }
                    override fun onResponse(call: Call<RefreshToken>, response: Response<RefreshToken>) {
                        if (response.isSuccessful) {
                            token = response.body()!!.token
                            sessionManager.deleteOldToken()
                            sessionManager.saveAuthToken(token)
                            Log.e("refreshed", "successfully ${sessionManager.fetchAuthToken()}")
                        } else {
                            Toast.makeText(context, "Произошла ошибка", Toast.LENGTH_SHORT).show()
                        }
                    }
    })
    }

    private fun banedUserDialog() {
        val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_5, null)
        val builder = AlertDialog.Builder(context)
                .setView(alertDialog)
                .show()
        alertDialog.custom_dialog_5_ok_button.setOnClickListener {
            builder.dismiss()
        }
    }

    private fun alreadyBookedDialog() {
        val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_3, null)
        val builder = AlertDialog.Builder(context)
                .setView(alertDialog)
                .show()
        alertDialog.custom_dialog_ok_button.setOnClickListener {
            builder.dismiss()
        }
    }

    private fun showDialog() {
        val alertDialog = LayoutInflater.from(context).inflate(R.layout.book_alert_dialog, null)
        val builder = AlertDialog.Builder(context)
            .setView(alertDialog)
            .show()
        alertDialog.book_alert_btn_ok.setOnClickListener {
            builder.dismiss()
            navController.navigate(R.id.action_bookingFragment2_to_playgroundListFragment)
        }
    }

    private fun inputCheck(name: String, number: String,
                           desiredTimeFrom: String,
                           desiredTimeUntil: String,
                           desiredDate: String) : Boolean {
        return !(name.isEmpty()|| number.isEmpty() || desiredTimeFrom.isEmpty() || desiredTimeUntil.isEmpty() || desiredDate.isEmpty())
    }


    private fun chooseTimeFrom(bookingTime: TextView) : String{
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.desired_time_from)
        builder.setSingleChoiceItems(desiredTimeArray.toTypedArray(), -1) { dialogInteface, i ->
            desiredTimeFrom = desiredTimeArray[i].toString()
            bookingTime.text = desiredTimeFrom
            bookingTime.setTextColor(Color.parseColor("#000000"))
            Handler(Looper.getMainLooper()).postDelayed({
                dialogInteface.dismiss()
            }, 600)
        }
        val dialog = builder.create().show()
        return desiredTimeFrom.toString()

    }
    private fun chooseTimeUntil(bookingTime: TextView) : String{
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.desired_time_from)
        builder.setSingleChoiceItems(desiredTimeArray.toTypedArray(),-1) { dialogInteface, i ->
            desiredTimeUntil = desiredTimeArray[i].toString()
            bookingTime.text = desiredTimeUntil
            bookingTime.setTextColor(Color.parseColor("#000000"))

            Handler(Looper.getMainLooper()).postDelayed({
                dialogInteface.dismiss()
            }, 600)
        }

        val dialog = builder.create().show()

        return desiredTimeUntil.toString()
    }



    private fun chooseDate() {
        val datePickerDialog = DatePickerDialog(requireContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
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
            booking_date_tv.text = desiredDate
            booking_date_frame.setBackgroundResource(R.drawable.background_rectangle_black)
            //filter_settings_fragment_desired_date_text.setTextColor(Color.parseColor("#000000"))
            //filter_settings_fragment_desired_date_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24_black)
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }
}
