package fieldleas.app.ui.main.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_personal_information.*
import kotlinx.android.synthetic.main.fragment_personal_information.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.models.auth.UserCreate
import fieldleas.app.models.auth.UserCreateResponse
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager

class PersonalInformationFragment: Fragment() {
    private val TAG = "PersonalInfoFragment"
    lateinit var navController: NavController
    lateinit var token: String
    lateinit var sessionManager: SessionManager
    lateinit var progressBar: ProgressBar
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_personal_information, container, false)
        progressBar = rootView.personal_info_progress_bar
        sessionManager = SessionManager(requireContext())
        token = sessionManager.fetchAuthToken().toString()

        if(!sessionManager.fetchUserName().equals("")) {
            rootView.personal_info_edit_text.setText(sessionManager.fetchUserName())
        }
        rootView.personal_info_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                rootView.personal_info_button_change.setBackgroundResource(R.drawable.background_rectangle_green)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        rootView.personal_info_button_change.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            updateName(personal_info_edit_text.text.toString())
        }
        rootView.personal_info_back_button.setOnClickListener {
            activity?.onBackPressed()
        }


        return rootView
    }

    private fun updateName(userName: String) {
        if (userName.isEmpty()) {
            Toast.makeText(context, "Не удалось изменить имя, попробуйте еще раз", Toast.LENGTH_SHORT).show()
        } else {
            val retrofitInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
            val user = UserCreate(sessionManager.fetchUserPhoneNumber().toString(), userName, sessionManager.fetchUserType()!!)
            val call = retrofitInstance.updateClientData("Bearer $token", user)
            call.enqueue(object : Callback<UserCreateResponse> {
                override fun onFailure(call: Call<UserCreateResponse>, t: Throwable) {
                    Log.e("this", t.message.toString())
                }
                override fun onResponse(call: Call<UserCreateResponse>, response: Response<UserCreateResponse>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val userInfo = response.body()
                        sessionManager.saveContactInfo(userInfo!!.id, userInfo.full_name, userInfo.phone_number, userInfo.type)
                        requireActivity().onBackPressed()
                    } else {
                        Toast.makeText(context, "Не удалось изменить имя, попробуйте еще раз", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }
}
