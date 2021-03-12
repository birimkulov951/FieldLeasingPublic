package fieldleas.app.ui.main.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.custom_dialog_2.view.*
import kotlinx.android.synthetic.main.custom_dialog_3.view.*
import kotlinx.android.synthetic.main.fragment_add_field.*
import kotlinx.android.synthetic.main.fragment_add_field.view.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import fieldleas.app.MainActivity
import com.example.fieldleasingpublic.R
import fieldleas.app.utils.UploadRequestBody
import fieldleas.app.models.addfield.*
import fieldleas.app.models.fields.FieldListItem
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager
import fieldleas.app.utils.Constants.FR
import fieldleas.app.utils.Constants.IF_EDIT_FRAGMENT
import fieldleas.app.utils.Constants.MN
import fieldleas.app.utils.Constants.SA
import fieldleas.app.utils.Constants.SN
import fieldleas.app.utils.Constants.TH
import fieldleas.app.utils.Constants.TU
import fieldleas.app.utils.Constants.WD
import fieldleas.app.utils.getFileName
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AddFieldFragment : Fragment(), UploadRequestBody.UploadCallback  {

    private val TAG = "AddStadiumFragment"
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    /**Lists*/
    private val desiredTimeArray = arrayOf(
            "00:00",
            "01:00",
            "02:00",
            "03:00",
            "04:00",
            "05:00",
            "06:00",
            "07:00",
            "08:00",
            "09:00",
            "10:00",
            "11:00",
            "12:00",
            "13:00",
            "14:00",
            "15:00",
            "16:00",
            "17:00",
            "18:00",
            "19:00",
            "20:00",
            "21:00",
            "22:00",
            "23:00"
    )

    private var isButtonClickable = false

    /**Vars to POST field*/
    private var fieldType = ""
    private var fieldName = ""
    private var fieldDescription = ""
    private var fieldAddress = ""
    private var fieldMaxPlayerQuantity = 0
    private var fieldWidth = ""
    private var fieldLength = ""
    private var fieldPhone = ""
    private var fieldPrice = ""
    private var hasParking = false
    private var isIndoor = false
    private var hasShowers = false
    private var hasLockerRooms = false
    private var hasLights = false
    private var hasRostrum = false
    private var hasEquipment = false


    // Field's open time
    private var monday = false
    private var tuesday = false
    private var wednesday = false
    private var thursday = false
    private var friday = false
    private var saturday = false
    private var sunday = false

    private var mondayStart = "08:00"
    private var mondayEnd = "23:00"
    private var tuesdayStart = "08:00"
    private var tuesdayEnd = "23:00"
    private var wednesdayStart = "08:00"
    private var wednesdayEnd = "23:00"
    private var thursdayStart = "08:00"
    private var thursdayEnd = "23:00"
    private var fridayStart = "08:00"
    private var fridayEnd = "23:00"
    private var saturdayStart ="08:00"
    private var saturdayEnd = "23:00"
    private var sundayStart = "08:00"
    private var sundayEnd = "23:00"


    private var fileUris = ArrayList<Uri>()
    private var deletedUri = ArrayList<Int>()
    private var editFieldId = -1
    private lateinit var builder: AlertDialog
    private lateinit var builder2: AlertDialog
    //private var isHiddenField = false
    private var disableBooking = false
    private var fieldTypeText = ""



    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val rootView: View =  inflater.inflate(R.layout.fragment_add_field, container, false)
        sessionManager = SessionManager(requireContext())

        // Received data from sub fragments
        val receiver = this.arguments
        if (receiver != null) {
            editFieldId = receiver.getInt(IF_EDIT_FRAGMENT, -1)
            rootView.add_field_fragment_label.text = getString(R.string.edit_stadium)
            // hide add field buttons and show edit field buttons
            rootView.add_field_fragment_if_edit_field.visibility = View.VISIBLE
            rootView.add_field_fragment_if_add_field.visibility = View.GONE


            val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog,null)
            builder2= AlertDialog.Builder(context).setView(alertDialog).show()


            retrofit2Observer(editFieldId)

        }

        if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE
                )
            }
        }

        /**FieldName EditText listener*/
        rootView.add_field_fragment_edit_text_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString() != "") {
                    fieldName = s.toString()
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_name.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    fieldName = ""
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_name.setBackgroundResource(R.drawable.background_stroke_grey)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /**FieldType spinner listener*/
        rootView.add_field_fragment_card_view_1.setOnClickListener{
            hideKeyboard(rootView)
            // setup the alert builder
            val builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.field_type))

            builder.setSingleChoiceItems((activity as MainActivity).fieldTypeNamesList.toTypedArray(), -1) { dialogInteface, i ->
                //set text
                fieldType =  "${(i + 1)}"
                fieldTypeText = "${(i + 1)}"
                rootView.add_field_fragment_card_view_1_text.text = (activity as MainActivity).fieldTypeNamesList.toTypedArray()[i]
                rootView.add_field_fragment_card_view_1_text.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_card_view_1_background.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_card_view_1_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24_black)
                // dismiss dialog
                Handler(Looper.getMainLooper()).postDelayed({
                    dialogInteface.dismiss()
                    clickPermission(rootView)
                }, 600)

            }
            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()

        }

        /**FieldDescription EditText listener*/
        rootView.add_field_fragment_edit_text_description.addTextChangedListener(object :
                TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString() != "") {
                    fieldDescription = s.toString()
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_description.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    fieldDescription = ""
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_description.setBackgroundResource(R.drawable.background_stroke_grey)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /**Add photos listener*/
        rootView.add_field_fragment_card_view_2.setOnClickListener{
            Toast.makeText(context, getString(R.string.long_press_to_add), Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent,getString(R.string.select_images)), IMAGE_PICK_CODE)
        }

        /**FieldAddress EditText listener*/
        rootView.add_field_fragment_edit_text_address.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString() != "") {
                    fieldAddress = s.toString()
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_address.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    fieldAddress = ""
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_address.setBackgroundResource(R.drawable.background_stroke_grey)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /** Add days and working hours for field owner */
        daysAdjustment(rootView)
        hoursStartAdjustment(rootView)
        hoursEndAdjustment(rootView)

        /**FieldMaxPlayerQuantity EditText listener*/
        rootView.add_field_fragment_edit_text_max_players.addTextChangedListener(object :
                TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString() != "") {
                    fieldMaxPlayerQuantity = s.toString().toInt()
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_max_players.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    fieldMaxPlayerQuantity = 0
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_max_players.setBackgroundResource(R.drawable.background_stroke_grey)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /**FieldWidth EditText listener*/
        rootView.add_field_fragment_edit_text_field_width.addTextChangedListener(object :
                TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString() != "") {
                    fieldWidth = s.toString()
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_field_width.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    fieldWidth = ""
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_field_width.setBackgroundResource(R.drawable.background_stroke_grey)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /**FieldLength EditText listener*/
        rootView.add_field_fragment_edit_text_field_length.addTextChangedListener(object :
                TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString() != "") {
                    fieldLength = s.toString()
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_field_length.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    fieldLength = ""
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_field_length.setBackgroundResource(R.drawable.background_stroke_grey)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /**FieldPhone EditText listener*/
        rootView.add_field_fragment_edit_text_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString() != "") {
                    fieldPhone = s.toString()
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_phone.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    fieldPhone = ""
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_phone.setBackgroundResource(R.drawable.background_stroke_grey)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /**Field price EditText listener*/
        rootView.add_field_fragment_edit_text_price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString() != "") {
                    fieldPrice = s.toString()
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_phone.setBackgroundResource(R.drawable.background_rectangle_black)
                } else {
                    fieldPrice = ""
                    clickPermission(rootView)
                    rootView.add_field_fragment_edit_text_phone.setBackgroundResource(R.drawable.background_stroke_grey)
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        /**Radio group*/
        rootView.add_field_fragment_radio_group.setOnCheckedChangeListener{ _, checkedId ->
            if(checkedId == R.id.add_field_fragment_radio_button_yes) {
                hasParking = true
                hideKeyboard(rootView)
            } else  {
                hasParking = false
                hideKeyboard(rootView)
            }
        }

        /**Radio group 2*/
        rootView.add_field_fragment_radio_group_2.setOnCheckedChangeListener{ _, checkedId ->
            if(checkedId == R.id.add_field_fragment_radio_button_yes_2) {
                isIndoor = true
                hideKeyboard(rootView)
            } else  {
                isIndoor = false
                hideKeyboard(rootView)
            }
        }

        /**hasShowers check box*/
        rootView.add_field_fragment_showers_check_box.setOnClickListener{
            hideKeyboard(rootView)
            hasShowers = add_field_fragment_showers_check_box.isChecked
        }
        /**hasLockerRooms check box*/
        rootView.add_field_fragment_locker_rooms_has_box.setOnClickListener{
            hideKeyboard(rootView)
            hasLockerRooms = add_field_fragment_locker_rooms_has_box.isChecked
        }
        /**hasLights check box*/
        rootView.add_field_fragment_lights_check_box.setOnClickListener{
            hideKeyboard(rootView)
            hasLights = add_field_fragment_lights_check_box.isChecked
        }
        /**hasRostrum check box*/
        rootView.add_field_fragment_rostrum_check_box.setOnClickListener{
            hideKeyboard(rootView)
            hasRostrum = add_field_fragment_rostrum_check_box.isChecked
        }
        /**hasEquipment check box*/
        rootView.add_field_fragment_equipment_check_box.setOnClickListener{
            hideKeyboard(rootView)
            hasEquipment = add_field_fragment_equipment_check_box.isChecked
        }

        /** Image listeners */
        rootView.add_field_fragment_card_view_photo_1_delete.setOnClickListener {
            add_field_fragment_card_view_photo_1.visibility = View.GONE
            if (fileUris.size != 0) {
                deletedUri.add(0)
            }
        }
        rootView.add_field_fragment_card_view_photo_3_delete.setOnClickListener {
            add_field_fragment_card_view_photo_3.visibility = View.GONE
            if (fileUris.size != 0) {
                deletedUri.add(1)
            }
        }
        rootView.add_field_fragment_card_view_photo_4_delete.setOnClickListener {
            add_field_fragment_card_view_photo_4.visibility = View.GONE
            if (fileUris.size != 0) {
                deletedUri.add(2)
            }
        }
        rootView.add_field_fragment_card_view_photo_5_delete.setOnClickListener {
            add_field_fragment_card_view_photo_5.visibility = View.GONE
            if (fileUris.size != 0) {
                deletedUri.add(3)
            }
        }
        rootView.add_field_fragment_card_view_photo_6_delete.setOnClickListener {
            add_field_fragment_card_view_photo_6.visibility = View.GONE
            if (fileUris.size != 0) {
                deletedUri.add(4)
            }
        }


        /** Cancel Button*/
        rootView.add_field_fragment_button_cancel.setOnClickListener{
            hideKeyboard(rootView)
            // Custom Alert Dialog
            val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_2, null)
            val alertTitle = alertDialog.findViewById<TextView>(R.id.alert_title)
            alertDialog.custom_dialog_ok_button.text = getString(R.string.ok)
            alertDialog.custom_dialog_2_btn_cancel.text = getString(R.string.cancel)
            alertTitle.text = getString(R.string.sure_to_cancel)

            val builder = AlertDialog.Builder(context).setView(alertDialog).show()

            alertDialog.custom_dialog_2_btn_cancel.setOnClickListener {
                builder.dismiss()
            }
            alertDialog.custom_dialog_ok_button.setOnClickListener {
                builder.dismiss()
                (activity as MainActivity).onBackPressed()
            }
        }

        /** Add Button*/
        rootView.add_field_fragment_button_add.setOnClickListener{
            clickPermission(rootView)

            if (!monday && !tuesday && !wednesday && !thursday && !friday && !saturday && !sunday) {

                Toast.makeText(context, getString(R.string.input_any_day), Toast.LENGTH_SHORT).show()

            } else if (isButtonClickable) {
                hideKeyboard(rootView)
                addField()
            }

        }

        /** IsHidden check box*/
        /*rootView.add_field_fragment_hide_field_check_box.setOnClickListener{
            hideKeyboard(rootView)
            isHiddenField = add_field_fragment_hide_field_check_box.isChecked
        }*/
        /** DisableBooking check box*/
        rootView.add_field_fragment_disable_booking_field_check_box.setOnClickListener{
            hideKeyboard(rootView)
            disableBooking = add_field_fragment_disable_booking_field_check_box.isChecked
        }


        /** Edit Button*/
        rootView.add_field_fragment_button_edit.setOnClickListener{
            hideKeyboard(rootView)
            clickPermission(rootView)

            if (!monday && !tuesday && !wednesday && !thursday && !friday && !saturday && !sunday) {

                Toast.makeText(context, getString(R.string.input_any_day), Toast.LENGTH_SHORT).show()

            } else if (isButtonClickable && editFieldId != -1) {

                editField()

            }

        }

        /** delete Button*/
        rootView.add_field_fragment_button_delete_field.setOnClickListener{
            hideKeyboard(rootView)
            clickPermission(rootView)

            val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_2,null)
            val builder = AlertDialog.Builder(context).setView(alertDialog).show()
            alertDialog.custom_dialog_2_alert_title.text = getString(R.string.delete_field_msg)

            alertDialog.custom_dialog_2_btn_cancel.setOnClickListener {
                builder.dismiss()
            }

            alertDialog.custom_dialog_2_btn_ok.setOnClickListener {
                builder.dismiss()
                deleteField()
            }

        }



        return rootView
    }


    private fun addField() {
        clearFileUriList()
        if (!fieldPhone.contains("+") || fieldPhone.length < 13 || fieldPhone.length > 13) {
            Toast.makeText(context, getString(R.string.phone_format), Toast.LENGTH_SHORT).show()
        } else if (fileUris.isEmpty()) {
            Toast.makeText(context, getString(R.string.select_images), Toast.LENGTH_SHORT).show()
        } else {

            val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog,null)
            builder = AlertDialog.Builder(context).setView(alertDialog).show()
            builder.setCanceledOnTouchOutside(false);

            val typePart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldType)
            val namePart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldName)
            val pricePart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldPrice)
            val minimumSizePart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldWidth)
            val maximumSizePart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldLength)
            val locationPart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldAddress)
            val descriptionPart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldDescription)

            val numberOfPlayersPart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldMaxPlayerQuantity.toString())
            val hasParkingPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasParking.toString())
            val isIndoorPart: RequestBody = RequestBody.create(MultipartBody.FORM, isIndoor.toString())
            val hasShowersPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasShowers.toString())
            val hasLockerRoomsPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasLockerRooms.toString())
            val hasLightsPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasLights.toString())
            val hasRostrumPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasRostrum.toString())
            val hasEquipmentPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasEquipment.toString())
            val disableBooking: RequestBody = RequestBody.create(MultipartBody.FORM, disableBooking.toString())
            val phoneNumberPart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldPhone)

            val imageParts: ArrayList<MultipartBody.Part> = ArrayList()
            for (i in 0 until fileUris.size) {
                val parcelFileDescriptor = (activity as MainActivity).contentResolver?.openFileDescriptor(fileUris[i], "r", null) ?: return

                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val file = File(activity?.cacheDir, (activity as MainActivity).contentResolver.getFileName(fileUris[i]))
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)

                val body = UploadRequestBody(file, "image", this)

                imageParts.add(MultipartBody.Part.createFormData("images", file.name, body))
            }

            val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
            val call = retroInstance.createField("Bearer ${sessionManager.fetchAuthToken()}", typePart, namePart, pricePart, minimumSizePart, maximumSizePart, imageParts, locationPart,
                    descriptionPart, numberOfPlayersPart, hasParkingPart, isIndoorPart, hasShowersPart, hasLockerRoomsPart, hasLightsPart, hasRostrumPart, hasEquipmentPart, disableBooking, phoneNumberPart)

            call.enqueue(object : Callback<AddFieldResponse> {

                override fun onResponse(call: Call<AddFieldResponse>, response: Response<AddFieldResponse>) {

                    if (response.isSuccessful) {

                        Log.e(TAG, "onResponse: ${response.body()}")

                        addFieldHours(response.body()?.id)

                    } else {
                        Log.e(TAG, "onResponse: ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<AddFieldResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: $t")
                    Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                    builder.dismiss()
                }

            })

        }
    }

    private fun clearFileUriList() {
        if (deletedUri.contains(4)) {
            fileUris.removeAt(4)
        }
        if (deletedUri.contains(3)) {
            fileUris.removeAt(3)
        }
        if (deletedUri.contains(2)) {
            fileUris.removeAt(2)
        }
        if (deletedUri.contains(1)) {
            fileUris.removeAt(1)
        }
        if (deletedUri.contains(0)) {
            fileUris.removeAt(0)
        }
    }

    private fun addFieldHours(fieldId: Int?) {

        val workingHour = ArrayList<WorkingHourX>()

        if (monday) {
            workingHour.add(WorkingHourX(MN, mondayStart, mondayEnd))
        }
        if (tuesday) {
            workingHour.add(WorkingHourX(TU, tuesdayStart, tuesdayEnd))
        }
        if (wednesday) {
            workingHour.add(WorkingHourX(WD, wednesdayStart, wednesdayEnd))
        }
        if (thursday) {
            workingHour.add(WorkingHourX(TH, thursdayStart, thursdayEnd))
        }
        if (friday) {
            workingHour.add(WorkingHourX(FR, fridayStart, fridayEnd))
        }
        if (saturday) {
            workingHour.add(WorkingHourX(SA, saturdayStart, saturdayEnd))
        }
        if (sunday) {
            workingHour.add(WorkingHourX(SN, sundayStart, sundayEnd))
        }

        val requestBody = AddHoursRequest(fieldId,workingHour as List<WorkingHourX>)

        val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        val call = retroInstance.createFieldHours("Bearer ${sessionManager.fetchAuthToken()}", requestBody)

        call.enqueue(object : Callback<AddFieldHoursResponse> {

            override fun onResponse(call: Call<AddFieldHoursResponse>, response: Response<AddFieldHoursResponse>) {

                if (response.isSuccessful && response.body()!!.success == true) {

                    builder.dismiss()

                    val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_3,null)
                    val builder = AlertDialog.Builder(context).setView(alertDialog).show()
                    builder.setCanceledOnTouchOutside(false);
                    alertDialog.custom_dialog_alert_title.text = getString(R.string.thank_you_field_created)

                    alertDialog.custom_dialog_ok_button.setOnClickListener {
                        builder.dismiss()
                        (activity as MainActivity).onBackPressed()
                    }

                    Log.e(TAG, "onResponse: ${response.body()}")

                } else {
                    Log.e(TAG, "onResponse: ${response.body()}")
                    Toast.makeText(context, getString(R.string.working_hours_not_added), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddFieldHoursResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
                builder.dismiss()
                Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun editField() {
        clearFileUriList()
        if (!fieldPhone.contains("+") || fieldPhone.length < 13 || fieldPhone.length > 13) {
            Toast.makeText(context, getString(R.string.phone_format), Toast.LENGTH_SHORT).show()
        }
        /*else if (fileUris.isEmpty()) {
            Toast.makeText(context, getString(R.string.select_images), Toast.LENGTH_SHORT).show()
        } */

        else {

            val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog,null)
            builder = AlertDialog.Builder(context).setView(alertDialog).show()
            builder.setCanceledOnTouchOutside(false)

            val typePart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldTypeText)
            val namePart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldName)
            val pricePart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldPrice)
            val minimumSizePart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldWidth)
            val maximumSizePart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldLength)
            val locationPart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldAddress)
            val descriptionPart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldDescription)

            val numberOfPlayersPart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldMaxPlayerQuantity.toString())
            val hasParkingPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasParking.toString())
            val isIndoorPart: RequestBody = RequestBody.create(MultipartBody.FORM, isIndoor.toString())
            val hasShowersPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasShowers.toString())
            val hasLockerRoomsPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasLockerRooms.toString())
            val hasLightsPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasLights.toString())
            val hasRostrumPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasRostrum.toString())
            val hasEquipmentPart: RequestBody = RequestBody.create(MultipartBody.FORM, hasEquipment.toString())
            val disableBookingPart: RequestBody = RequestBody.create(MultipartBody.FORM, disableBooking.toString())
            //val isHiddenPart: RequestBody = RequestBody.create(MultipartBody.FORM, isHiddenField.toString())
            val phoneNumberPart: RequestBody = RequestBody.create(MultipartBody.FORM, fieldPhone)

            val imageParts: ArrayList<MultipartBody.Part> = ArrayList()
            for (i in 0 until fileUris.size) {
                val parcelFileDescriptor = (activity as MainActivity).contentResolver?.openFileDescriptor(fileUris[i], "r", null) ?: return

                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val file = File(activity?.cacheDir, (activity as MainActivity).contentResolver.getFileName(fileUris[i]))
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)

                val body = UploadRequestBody(file, "image", this)

                imageParts.add(MultipartBody.Part.createFormData("images", file.name, body))
            }

            val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

            lateinit var call: Call<AddFieldResponse>

            Log.e(TAG, "editField: ${fileUris.isEmpty()}")
            if (fileUris.isEmpty()) {
                call = retroInstance.updateField("Bearer ${sessionManager.fetchAuthToken()}", typePart, namePart, pricePart, minimumSizePart, maximumSizePart, locationPart,
                        descriptionPart, numberOfPlayersPart, hasParkingPart, isIndoorPart, hasShowersPart, hasLockerRoomsPart, hasLightsPart, hasRostrumPart, hasEquipmentPart,
                        disableBookingPart, editFieldId, phoneNumberPart)
            } else {
                call = retroInstance.updateField("Bearer ${sessionManager.fetchAuthToken()}", typePart, namePart, pricePart, minimumSizePart, maximumSizePart, imageParts ,locationPart,
                        descriptionPart, numberOfPlayersPart, hasParkingPart, isIndoorPart, hasShowersPart, hasLockerRoomsPart, hasLightsPart, hasRostrumPart, hasEquipmentPart,
                        disableBookingPart, editFieldId, phoneNumberPart)
            }


            call.enqueue(object : Callback<AddFieldResponse> {

                override fun onResponse(call: Call<AddFieldResponse>, response: Response<AddFieldResponse>) {

                    if (response.isSuccessful) {

                        Log.e(TAG, "onResponse: ${response.body()}")

                        editFieldHours()

                    } else {
                        Log.e(TAG, "onResponse: ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<AddFieldResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                    Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun editFieldHours() {
        val workingHour = ArrayList<EditWorkingHoursRequest>()
        if (monday) {
            workingHour.add(EditWorkingHoursRequest(MN, mondayStart, mondayEnd))
        }
        if (tuesday) {
            workingHour.add(EditWorkingHoursRequest(TU, tuesdayStart, tuesdayEnd))
        }
        if (wednesday) {
            workingHour.add(EditWorkingHoursRequest(WD, wednesdayStart, wednesdayEnd))
        }
        if (thursday) {
            workingHour.add(EditWorkingHoursRequest(TH, thursdayStart, thursdayEnd))
        }
        if (friday) {
            workingHour.add(EditWorkingHoursRequest(FR, fridayStart, fridayEnd))
        }
        if (saturday) {
            workingHour.add(EditWorkingHoursRequest(SA, saturdayStart, saturdayEnd))
        }
        if (sunday) {
            workingHour.add(EditWorkingHoursRequest(SN, sundayStart, sundayEnd))
        }

        val requestBody = EditHoursRequest(editFieldId,workingHour as List<EditWorkingHoursRequest>)

        val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        val call = retroInstance.updateFieldHours("Bearer ${sessionManager.fetchAuthToken()}", requestBody, editFieldId)

        call.enqueue(object : Callback<AddFieldHoursResponse> {

            override fun onResponse(call: Call<AddFieldHoursResponse>, response: Response<AddFieldHoursResponse>) {

                if (response.isSuccessful && response.body()!!.success == true) {
                    
                    builder.dismiss()

                    val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_3,null)
                    val builder = AlertDialog.Builder(context).setView(alertDialog).show()
                    builder.setCanceledOnTouchOutside(false);
                    alertDialog.custom_dialog_alert_title.text = getString(R.string.your_field_have_been_edited)

                    alertDialog.custom_dialog_ok_button.setOnClickListener {
                        builder.dismiss()
                        (activity as MainActivity).onBackPressed()
                    }

                    Log.e(TAG, "onResponse: ${response.body()}")

                } else {
                    Log.e(TAG, "onResponse: ${response.body()}")
                    Toast.makeText(context, getString(R.string.working_hours_not_added), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AddFieldHoursResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
                Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun deleteField() {

        val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        val call = retroInstance.deleteField(editFieldId)

        call.enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                if (response.isSuccessful) {

                    val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_3,null)
                    val builder = AlertDialog.Builder(context).setView(alertDialog).show()
                    builder.setCanceledOnTouchOutside(false);
                    alertDialog.custom_dialog_alert_title.text = getString(R.string.successfully_deleted)
                    alertDialog.custom_dialog_ok_button.setOnClickListener {
                        builder.dismiss()
                        (activity as MainActivity).onBackPressed()
                    }

                } else {
                    Log.e(TAG, "onResponse: ${response.body()}")
                    Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
                Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun retrofit2Observer(fieldId: Int) {
        val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        val call = retroInstance.getFieldListItemById("Bearer ${sessionManager.fetchAuthToken()}", fieldId)

        call.enqueue(object : Callback<FieldListItem> {

            override fun onResponse(call: Call<FieldListItem>, response: Response<FieldListItem>) {

                if (response.isSuccessful) {

                    fieldType = (activity as MainActivity).fieldTypeNamesList[response.body()?.fieldType!! - 1]
                    fieldTypeText = (response.body()?.fieldType!!).toString()
                    fieldName = response.body()?.name!!
                    fieldDescription = response.body()?.description!!
                    fieldAddress = response.body()?.location!!
                    fieldMaxPlayerQuantity = response.body()?.numberOfPlayers!!
                    fieldWidth = response.body()?.minimumSize.toString()
                    fieldLength = response.body()?.maximumSize.toString()
                    fieldPhone = response.body()?.phoneNumber!!
                    fieldPrice = response.body()?.price!!
                    hasParking = response.body()?.hasParking!!
                    isIndoor = response.body()?.isIndoor!!
                    hasShowers = response.body()?.hasShowers!!
                    hasLockerRooms = response.body()?.hasLockerRooms!!
                    hasLights = response.body()?.hasLights!!
                    hasRostrum = response.body()?.hasRostrum!!
                    hasEquipment = response.body()?.hasEquipment!!

                    //isHiddenField = response.body()?.isHidden!!
                    disableBooking = response.body()?.disableBooking!!

                    for (it in response.body()?.images!!.indices) {
                        if (response.body()?.images?.get(0) != null) {
                            glideUrlLoad(
                                    response.body()?.images?.get(0)!!.image,
                                    add_field_fragment_card_view_photo_1_image
                            )
                            add_field_fragment_card_view_photo_1.visibility = View.VISIBLE
                        } else if (response.body()?.images?.get(1) != null) {
                            glideUrlLoad(
                                    response.body()?.images?.get(1)!!.image,
                                    add_field_fragment_card_view_photo_3_image
                            )
                            add_field_fragment_card_view_photo_3.visibility = View.VISIBLE
                        } else if (response.body()?.images?.get(2) != null) {
                            glideUrlLoad(
                                    response.body()?.images?.get(2)!!.image,
                                    add_field_fragment_card_view_photo_4_image
                            )
                            add_field_fragment_card_view_photo_4.visibility = View.VISIBLE
                        } else if (response.body()?.images?.get(3) != null) {
                            glideUrlLoad(
                                    response.body()?.images?.get(3)!!.image,
                                    add_field_fragment_card_view_photo_5_image
                            )
                            add_field_fragment_card_view_photo_5.visibility = View.VISIBLE
                        } else if (response.body()?.images?.get(4) != null) {
                            glideUrlLoad(
                                    response.body()?.images?.get(4)!!.image,
                                    add_field_fragment_card_view_photo_6_image
                            )
                            add_field_fragment_card_view_photo_6.visibility = View.VISIBLE
                        }
                    }

                    val workingHoursResponse = response.body()?.workingHours

                    for (it in 0 until workingHoursResponse?.size!!) {
                        when (workingHoursResponse[it].day) {
                            getString(R.string.monday1) -> {
                                monday = true
                                mondayStart = workingHoursResponse[it].start.substring(0, 5)
                                mondayEnd = workingHoursResponse[it].end.substring(0, 5)
                            }
                            getString(R.string.tuesday1) -> {
                                tuesday = true
                                tuesdayStart = workingHoursResponse[it].start.substring(0, 5)
                                tuesdayEnd = workingHoursResponse[it].end.substring(0, 5)
                            }
                            getString(R.string.wednesday1) -> {
                                wednesday = true
                                wednesdayStart = workingHoursResponse[it].start.substring(0, 5)
                                wednesdayEnd = workingHoursResponse[it].end.substring(0, 5)
                            }
                            getString(R.string.thursday1) -> {
                                thursday = true
                                thursdayStart = workingHoursResponse[it].start.substring(0, 5)
                                thursdayEnd = workingHoursResponse[it].end.substring(0, 5)
                            }
                            getString(R.string.friday1) -> {
                                friday = true
                                fridayStart = workingHoursResponse[it].start.substring(0, 5)
                                fridayEnd = workingHoursResponse[it].end.substring(0, 5)
                            }
                            getString(R.string.saturday1) -> {
                                saturday = true
                                saturdayStart = workingHoursResponse[it].start.substring(0, 5)
                                saturdayEnd = workingHoursResponse[it].end.substring(0, 5)
                            }
                            getString(R.string.sunday1) -> {
                                sunday = true
                                sundayStart = workingHoursResponse[it].start.substring(0, 5)
                                sundayEnd = workingHoursResponse[it].end.substring(0, 5)
                            }
                        }

                        //Log.e(TAG, "onResponse: $disableBooking ${workingHoursResponse[it].day} ")

                    }

                    observeViews()

                    // Thread sleep
                    Handler(Looper.getMainLooper()).postDelayed({
                        builder2.dismiss()
                    }, 500)

                } else {
                    Log.e(TAG, "onResponse: ${response.body()}")
                    Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT)
                            .show()
                }

            }

            override fun onFailure(call: Call<FieldListItem>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
                Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT)
                        .show()
            }

        })
    }

    private fun glideUrlLoad(load: String, image: ImageView) {
        Glide.with(requireContext())
                .load(load)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .into(image);
    }
    private fun glideUrlLoad(load: Uri, image: ImageView) {
        Glide.with(requireContext())
                .load(load)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .into(image);
    }

    private fun observeViews() {
        add_field_fragment_card_view_1_text.text = fieldType
        add_field_fragment_card_view_1_text.setTextColor(Color.parseColor("#000000"))
        add_field_fragment_card_view_1_background.setBackgroundResource(R.drawable.background_rectangle_black)
        add_field_fragment_card_view_1_icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24_black)
        add_field_fragment_edit_text_name.setText(fieldName)
        add_field_fragment_edit_text_name.setBackgroundResource(R.drawable.background_rectangle_black)
        add_field_fragment_edit_text_description.setText(fieldDescription)
        add_field_fragment_edit_text_description.setBackgroundResource(R.drawable.background_rectangle_black)
        add_field_fragment_edit_text_address.setText(fieldAddress)
        add_field_fragment_edit_text_address.setBackgroundResource(R.drawable.background_rectangle_black)
        add_field_fragment_edit_text_max_players.setText(fieldMaxPlayerQuantity.toString())
        add_field_fragment_edit_text_max_players.setBackgroundResource(R.drawable.background_rectangle_black)
        add_field_fragment_edit_text_field_width.setText(fieldWidth)
        add_field_fragment_edit_text_field_width.setBackgroundResource(R.drawable.background_rectangle_black)
        add_field_fragment_edit_text_field_length.setText(fieldLength)
        add_field_fragment_edit_text_field_length.setBackgroundResource(R.drawable.background_rectangle_black)
        add_field_fragment_edit_text_phone.setText(fieldPhone)
        add_field_fragment_edit_text_phone.setBackgroundResource(R.drawable.background_rectangle_black)
        add_field_fragment_edit_text_price.setText(fieldPrice)
        add_field_fragment_edit_text_price.setBackgroundResource(R.drawable.background_rectangle_black)

        if (hasParking) {
            add_field_fragment_radio_button_yes.isChecked = true
        }
        if (isIndoor) {
            add_field_fragment_radio_button_yes_2.isChecked = true
        }
        if (hasShowers) {
            add_field_fragment_showers_check_box.isChecked = true
        }
        if (hasLockerRooms) {
            add_field_fragment_locker_rooms_has_box.isChecked = true
        }
        if (hasLights) {
            add_field_fragment_lights_check_box.isChecked = true
        }
        if (hasRostrum) {
            add_field_fragment_rostrum_check_box.isChecked = true
        }
        if (hasEquipment) {
            add_field_fragment_equipment_check_box.isChecked = true
        }
        if (disableBooking) {
            add_field_fragment_disable_booking_field_check_box.isChecked = true
        }
        /*if (isHiddenField) {
            add_field_fragment_hide_field_check_box.isChecked = true
        }*/
        if (monday) {
            add_field_fragment_spinner_monday_start.text = mondayStart
            add_field_fragment_spinner_monday_end.text = mondayEnd
            add_field_fragment_text_monday.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_text_monday.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_monday_start.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_monday_start.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_monday_end.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_monday_end.setBackgroundResource(R.drawable.background_rectangle_black)
        }
        if (tuesday) {
            add_field_fragment_spinner_tuesday_start.text = tuesdayStart
            add_field_fragment_spinner_tuesday_end.text = tuesdayEnd
            add_field_fragment_text_tuesday.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_text_tuesday.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_tuesday_start.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_tuesday_start.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_tuesday_end.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_tuesday_end.setBackgroundResource(R.drawable.background_rectangle_black)

        }
        if (wednesday) {
            add_field_fragment_spinner_wednesday_start.text = wednesdayStart
            add_field_fragment_spinner_wednesday_end.text = wednesdayEnd
            add_field_fragment_text_wednesday.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_text_wednesday.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_wednesday_start.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_wednesday_start.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_wednesday_end.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_wednesday_end.setBackgroundResource(R.drawable.background_rectangle_black)
        }
        if (thursday) {
            add_field_fragment_spinner_thursday_start.text = thursdayStart
            add_field_fragment_spinner_thursday_end.text = thursdayEnd
            add_field_fragment_text_thursday.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_text_thursday.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_thursday_start.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_thursday_start.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_thursday_end.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_thursday_end.setBackgroundResource(R.drawable.background_rectangle_black)
        }
        if (friday) {
            add_field_fragment_spinner_friday_start.text = fridayStart
            add_field_fragment_spinner_friday_end.text = fridayEnd
            add_field_fragment_text_friday.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_text_friday.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_friday_start.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_friday_start.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_friday_end.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_friday_end.setBackgroundResource(R.drawable.background_rectangle_black)
        }
        if (saturday) {
            add_field_fragment_spinner_saturday_start.text = saturdayStart
            add_field_fragment_spinner_saturday_end.text = saturdayEnd
            add_field_fragment_text_saturday.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_text_saturday.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_saturday_start.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_saturday_start.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_saturday_end.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_saturday_end.setBackgroundResource(R.drawable.background_rectangle_black)
        }
        if (sunday) {
            add_field_fragment_spinner_sunday_start.text = sundayStart
            add_field_fragment_spinner_sunday_end.text = sundayEnd
            add_field_fragment_text_sunday.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_text_sunday.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_sunday_start.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_sunday_start.setBackgroundResource(R.drawable.background_rectangle_black)
            add_field_fragment_spinner_sunday_end.setTextColor(Color.parseColor("#000000"))
            add_field_fragment_spinner_sunday_end.setBackgroundResource(R.drawable.background_rectangle_black)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fileUris = ArrayList()
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            fileUris.clear()
            deletedUri.clear()
            add_field_fragment_card_view_photo_1.visibility = View.GONE
            add_field_fragment_card_view_photo_3.visibility = View.GONE
            add_field_fragment_card_view_photo_4.visibility = View.GONE
            add_field_fragment_card_view_photo_5.visibility = View.GONE
            add_field_fragment_card_view_photo_6.visibility = View.GONE
            try {
                val clipData: ClipData = data?.clipData!!
                for (i in 0 until clipData.itemCount) {
                    val item = clipData.getItemAt(i)
                    val uri: Uri = item.uri
                    if (i == 0) {
                        glideUrlLoad(uri, add_field_fragment_card_view_photo_1_image)
                        add_field_fragment_card_view_photo_1.visibility = View.VISIBLE
                    } else if ( i == 1) {
                        glideUrlLoad(uri, add_field_fragment_card_view_photo_3_image)
                        add_field_fragment_card_view_photo_3.visibility = View.VISIBLE
                    } else if ( i == 2) {
                        glideUrlLoad(uri, add_field_fragment_card_view_photo_4_image)
                        add_field_fragment_card_view_photo_4.visibility = View.VISIBLE
                    } else if ( i == 3) {
                        glideUrlLoad(uri, add_field_fragment_card_view_photo_5_image)
                        add_field_fragment_card_view_photo_5.visibility = View.VISIBLE
                    }  else if ( i == 4) {
                        glideUrlLoad(uri, add_field_fragment_card_view_photo_6_image)
                        add_field_fragment_card_view_photo_6.visibility = View.VISIBLE
                    } else {
                        break
                    }
                    fileUris.add(uri)
                }

            } catch (e: NullPointerException) {
                Log.e(TAG, "catch: $e")

                val photo = data!!.data!!
                fileUris.add(photo)
                glideUrlLoad(photo, add_field_fragment_card_view_photo_1_image)
                add_field_fragment_card_view_photo_1.visibility = View.VISIBLE
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when(requestCode) {

            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //pickImageFromGallery()
                } else {
                    Toast.makeText(context, getString(R.string.no_permission), Toast.LENGTH_SHORT)
                            .show()
                }
            }
        }
    }

    private fun hoursStartAdjustment(rootView: View) {
        rootView.add_field_fragment_spinner_monday_start.setOnClickListener{
            if (monday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    mondayStart = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_monday_start.text = mondayStart
                    rootView.add_field_fragment_spinner_monday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_monday_start.setTextColor(Color.parseColor("#000000"))
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_tuesday_start.setOnClickListener{
            if (tuesday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    tuesdayStart = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_tuesday_start.text = tuesdayStart
                    rootView.add_field_fragment_spinner_tuesday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_tuesday_start.setTextColor(
                            Color.parseColor(
                                    "#000000"
                            )
                    )
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_wednesday_start.setOnClickListener{
            if (wednesday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    wednesdayStart = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_wednesday_start.text = wednesdayStart
                    rootView.add_field_fragment_spinner_wednesday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_wednesday_start.setTextColor(
                            Color.parseColor(
                                    "#000000"
                            )
                    )
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_thursday_start.setOnClickListener{
            if (thursday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    thursdayStart = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_thursday_start.text = thursdayStart
                    rootView.add_field_fragment_spinner_thursday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_thursday_start.setTextColor(
                            Color.parseColor(
                                    "#000000"
                            )
                    )
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_friday_start.setOnClickListener{
            if (friday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    fridayStart = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_friday_start.text = fridayStart
                    rootView.add_field_fragment_spinner_friday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_friday_start.setTextColor(Color.parseColor("#000000"))
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_saturday_start.setOnClickListener{
            if (saturday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    saturdayStart = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_saturday_start.text = saturdayStart
                    rootView.add_field_fragment_spinner_saturday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_saturday_start.setTextColor(
                            Color.parseColor(
                                    "#000000"
                            )
                    )
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_sunday_start.setOnClickListener{
            if (sunday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    sundayStart = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_sunday_start.text = sundayStart
                    rootView.add_field_fragment_spinner_sunday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_sunday_start.setTextColor(Color.parseColor("#000000"))
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun hoursEndAdjustment(rootView: View) {
        rootView.add_field_fragment_spinner_monday_end.setOnClickListener{
            if (monday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    mondayEnd = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_monday_end.text = mondayEnd
                    rootView.add_field_fragment_spinner_monday_end.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_monday_end.setTextColor(Color.parseColor("#000000"))
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_tuesday_end.setOnClickListener{
            if (tuesday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    tuesdayEnd = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_tuesday_end.text = tuesdayEnd
                    rootView.add_field_fragment_spinner_tuesday_end.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_tuesday_end.setTextColor(Color.parseColor("#000000"))
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_wednesday_end.setOnClickListener{
            if (wednesday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    wednesdayEnd = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_wednesday_end.text = wednesdayEnd
                    rootView.add_field_fragment_spinner_wednesday_end.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_wednesday_end.setTextColor(
                            Color.parseColor(
                                    "#000000"
                            )
                    )
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_thursday_end.setOnClickListener{
            if (thursday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    thursdayEnd = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_thursday_end.text = thursdayEnd
                    rootView.add_field_fragment_spinner_thursday_end.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_thursday_end.setTextColor(Color.parseColor("#000000"))
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_friday_end.setOnClickListener{
            if (friday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    fridayEnd = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_friday_end.text = fridayEnd
                    rootView.add_field_fragment_spinner_friday_end.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_friday_end.setTextColor(Color.parseColor("#000000"))
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_saturday_end.setOnClickListener{
            if (saturday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    saturdayEnd = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_saturday_end.text = saturdayEnd
                    rootView.add_field_fragment_spinner_saturday_end.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_saturday_end.setTextColor(Color.parseColor("#000000"))
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
        rootView.add_field_fragment_spinner_sunday_end.setOnClickListener{
            if (sunday) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.desired_time_from))

                builder.setSingleChoiceItems(desiredTimeArray, -1) { dialogInterface, i ->
                    //set text
                    sundayEnd = desiredTimeArray[i]
                    rootView.add_field_fragment_spinner_sunday_end.text = sundayEnd
                    rootView.add_field_fragment_spinner_sunday_end.setBackgroundResource(R.drawable.background_rectangle_black)
                    rootView.add_field_fragment_spinner_sunday_end.setTextColor(Color.parseColor("#000000"))
                    // dismiss dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialogInterface.dismiss()
                    }, 600)
                }
                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun daysAdjustment(rootView: View) {
        rootView.add_field_fragment_text_monday.setOnClickListener{

            if (!monday) {
                monday = true

                rootView.add_field_fragment_text_monday.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_text_monday.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_monday_start.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_monday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_monday_end.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_monday_end.setBackgroundResource(R.drawable.background_rectangle_black)

            } else {
                monday = false
                rootView.add_field_fragment_text_monday.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_text_monday.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_monday_start.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_monday_start.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_spinner_monday_end.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_monday_end.setBackgroundResource(R.drawable.background_stroke_grey)

            }
        }
        rootView.add_field_fragment_text_tuesday.setOnClickListener{

            if (!tuesday) {
                tuesday = true

                rootView.add_field_fragment_text_tuesday.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_text_tuesday.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_tuesday_start.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_tuesday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_tuesday_end.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_tuesday_end.setBackgroundResource(R.drawable.background_rectangle_black)

            } else {
                tuesday = false
                rootView.add_field_fragment_text_tuesday.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_text_tuesday.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_tuesday_start.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_tuesday_start.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_spinner_tuesday_end.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_tuesday_end.setBackgroundResource(R.drawable.background_stroke_grey)

            }
        }
        rootView.add_field_fragment_text_wednesday.setOnClickListener{

            if (!wednesday) {
                wednesday = true

                rootView.add_field_fragment_text_wednesday.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_text_wednesday.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_wednesday_start.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_wednesday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_wednesday_end.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_wednesday_end.setBackgroundResource(R.drawable.background_rectangle_black)

            } else {
                wednesday = false
                rootView.add_field_fragment_text_wednesday.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_text_wednesday.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_wednesday_start.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_wednesday_start.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_spinner_wednesday_end.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_wednesday_end.setBackgroundResource(R.drawable.background_stroke_grey)

            }
        }
        rootView.add_field_fragment_text_thursday.setOnClickListener{

            if (!thursday) {
                thursday = true

                rootView.add_field_fragment_text_thursday.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_text_thursday.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_thursday_start.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_thursday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_thursday_end.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_thursday_end.setBackgroundResource(R.drawable.background_rectangle_black)

            } else {
                thursday = false
                rootView.add_field_fragment_text_thursday.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_text_thursday.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_thursday_start.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_thursday_start.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_spinner_thursday_end.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_thursday_end.setBackgroundResource(R.drawable.background_stroke_grey)

            }
        }
        rootView.add_field_fragment_text_friday.setOnClickListener{

            if (!friday) {
                friday = true

                rootView.add_field_fragment_text_friday.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_text_friday.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_friday_start.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_friday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_friday_end.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_friday_end.setBackgroundResource(R.drawable.background_rectangle_black)

            } else {
                friday = false
                rootView.add_field_fragment_text_friday.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_text_friday.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_friday_start.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_friday_start.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_spinner_friday_end.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_friday_end.setBackgroundResource(R.drawable.background_stroke_grey)

            }
        }
        rootView.add_field_fragment_text_saturday.setOnClickListener{

            if (!saturday) {
                saturday = true

                rootView.add_field_fragment_text_saturday.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_text_saturday.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_saturday_start.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_saturday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_saturday_end.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_saturday_end.setBackgroundResource(R.drawable.background_rectangle_black)

            } else {
                saturday = false
                rootView.add_field_fragment_text_saturday.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_text_saturday.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_saturday_start.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_saturday_start.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_spinner_saturday_end.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_saturday_end.setBackgroundResource(R.drawable.background_stroke_grey)

            }
        }
        rootView.add_field_fragment_text_sunday.setOnClickListener{

            if (!sunday) {
                sunday = true

                rootView.add_field_fragment_text_sunday.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_text_sunday.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_sunday_start.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_sunday_start.setBackgroundResource(R.drawable.background_rectangle_black)
                rootView.add_field_fragment_spinner_sunday_end.setTextColor(Color.parseColor("#000000"))
                rootView.add_field_fragment_spinner_sunday_end.setBackgroundResource(R.drawable.background_rectangle_black)

            } else {
                sunday = false
                rootView.add_field_fragment_text_sunday.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_text_sunday.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_sunday_start.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_sunday_start.setBackgroundResource(R.drawable.background_stroke_grey)
                rootView.add_field_fragment_spinner_sunday_end.setTextColor(Color.parseColor("#C4C4C4"))
                rootView.add_field_fragment_spinner_sunday_end.setBackgroundResource(R.drawable.background_stroke_grey)

            }
        }
    }

    private fun hideKeyboard(rootView: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
    }

    private fun clickPermission(view: View?) {

        if (fieldType != "" && fieldName != "" && fieldDescription != "" && fieldAddress != "" && fieldMaxPlayerQuantity != 0 &&
                fieldWidth != "" &&  fieldLength != "" &&  fieldPhone != "" &&  fieldPrice != "") {
            isButtonClickable = true

            view?.add_field_fragment_button_add?.setBackgroundResource(R.drawable.background_rectangle_green)
            view?.add_field_fragment_button_edit?.setBackgroundResource(R.drawable.background_rectangle_green)

        } else {
            isButtonClickable = false
            view?.add_field_fragment_button_add?.setBackgroundResource(R.drawable.background_rectangle_grey)
            view?.add_field_fragment_button_edit?.setBackgroundResource(R.drawable.background_rectangle_grey)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onProgressUpdate(percentage: Int) {
        Log.e(TAG, "onProgressUpdate: $percentage")
    }

}