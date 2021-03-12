package fieldleas.app.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_profile_not_registered_users.view.*
import com.example.fieldleasingpublic.R
import fieldleas.app.network.SessionManager

class ProfileNotRegisteredUsers : Fragment() {

    private val TAG = "NotRegisteredUsers"
    private lateinit var sessionManager: SessionManager
    lateinit var navController : NavController
    private var isAuthorized = false
    private var type = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
            val rootView =
                    inflater.inflate(R.layout.fragment_profile_not_registered_users, container, false)
        sessionManager = SessionManager(requireContext())
       // sessionManager.saveAuthToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZV9udW1iZXIiOiIrOTk2NTA5ODE3ODE4IiwiZXhwIjoxNjA3NDg1NjA3fQ.T80XOoVmqwN3DdvQphAVPSKj7dq5gaJMVKwXwjKa-Bo")
        isAuthorized = sessionManager.fetchAuthToken() != null
        Log.e("is Authorized", isAuthorized.toString())
        Log.e(TAG, sessionManager.fetchAuthToken().toString())
        type = sessionManager.fetchUserType()!!



            rootView.profile_login.setOnClickListener {
                navController.navigate(R.id.action_profileNotRegisteredUsers_to_authorizationFragment2)
            }
            rootView.profile_register.setOnClickListener {

                navController.navigate(R.id.action_profileNotRegisteredUsers_to_registrationMainFragment)

        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        if (isAuthorized){
            if (type == 1){
                navController.navigate(R.id.action_profileNotRegisteredUsers_to_playgroundOwnerProfileFragment)
            }
            else if(type == 2){
                navController.navigate(R.id.action_profileNotRegisteredUsers_to_justUserProfileFragment)
            }
        }

    }
}