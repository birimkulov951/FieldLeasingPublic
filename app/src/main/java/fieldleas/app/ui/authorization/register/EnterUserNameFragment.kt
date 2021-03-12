package fieldleas.app.ui.authorization.register

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.custom_dialog_6.view.*
import kotlinx.android.synthetic.main.fragment_registration_enter_user_name.*
import kotlinx.android.synthetic.main.fragment_registration_enter_user_name.view.*
import retrofit2.Call
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.models.auth.*
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager

class EnterUserNameFragment: Fragment(){
    lateinit var navController: NavController
    lateinit var phoneNumber: String
    lateinit var sessionManager: SessionManager
    lateinit var progressBar: ProgressBar
    var type = 0
    lateinit var token: String
    override fun onCreateView(
                inflater: LayoutInflater, container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View? {

            val rootView = inflater.inflate(R.layout.fragment_registration_enter_user_name, container, false)
            rootView.registration_enter_user_name_edit_text.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(p0: Editable?) {
                    rootView.registration_button.setBackgroundResource(R.drawable.ic_button)
                    rootView.registration_button.setOnClickListener {
                        checkDataEntered(it)
                    }
                }
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
            })
        sessionManager = SessionManager(requireContext())
        progressBar = rootView.registration_progress_bar
        return rootView
    }


    private fun checkDataEntered(it: View) {
        if (isEmpty(registration_enter_user_name_edit_text)){
           registration_enter_user_name_edit_text.error = getString(R.string.field_should_be_filled)
        }else{
            val name = registration_enter_user_name_edit_text.text.toString()
            val bundle = this.arguments
            phoneNumber = bundle?.getString("phoneNumber").toString()
            type = bundle?.getInt("type")!!
            var registerRequest = type?.let { it1 -> UserCreate(phoneNumber, name, it1) }
            if (registerRequest != null) {
                register(registerRequest)
                progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun register(userCreate: UserCreate) {
        val retrofitInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)
        val call = retrofitInstance.register(userCreate)
        call.enqueue(object : retrofit2.Callback<UserCreateResponse> {
            override fun onFailure(call: Call<UserCreateResponse>, t: Throwable) {
                Log.e("register", t.message.toString())
                Toast.makeText(context, "Проверьте интернет соединение", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(
                    call: Call<UserCreateResponse>,
                    response: Response<UserCreateResponse>) {
                if (response.isSuccessful) {
                    val loginRequest = LoginRequest(userCreate.phone_number)
                    val log = retrofitInstance.login(loginRequest)
                    val user = response.body()
                    user?.id?.let {
                        sessionManager.saveContactInfo(it, user.full_name, user.phone_number, user.type)

                        log.enqueue(object : retrofit2.Callback<LoginResponse> {
                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                Log.e("login request", "failed at : ${t.message}")
                            }

                            override fun onResponse(
                                    call: Call<LoginResponse>,
                                    response: Response<LoginResponse>) {
                                if (response.isSuccessful) {
                                    token = response.body()?.token.toString()
                                    sessionManager.saveAuthToken(token)
                                    view?.let { Navigation.findNavController(it).navigate(R.id.action_enterUserNameFragment_to_profileNotRegisteredUsers) }
                                    Log.e("Token: ", "login response body token ${response.body()?.token}")
                                } else {
                                    Toast.makeText(context, "Не удалось войти", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                }
                else{
                    progressBar.visibility = View.GONE
                    showDialog()
               }
                    }
        })
    }

    private fun showDialog() {
        val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_6, null)
        val builder = AlertDialog.Builder(context)
                .setView(alertDialog)
                .show()
        alertDialog.custom_dialog_6_ok_button.setOnClickListener {
            builder.dismiss()
            requireActivity().onBackPressed()
        }
    }

    //if input fields are empty
    private fun isEmpty(text: EditText): Boolean {
        val str: CharSequence = text.text.toString().trim()
        return TextUtils.isEmpty(str)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }
}