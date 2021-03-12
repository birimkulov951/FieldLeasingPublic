package fieldleas.app.ui.authorization.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.fieldleasingpublic.R
import com.example.fieldleasingpublic.databinding.FragmentRegistrationMainBinding
import fieldleas.app.network.SessionManager

class RegistrationMainFragment : Fragment() {

    private var _binding: FragmentRegistrationMainBinding? = null
    private val binding get() = _binding

    private val TAG = "RegistrationFragment"
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager
    var type = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegistrationMainBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //save type to the bundle and after verification to the sharedpref
        val bundle = Bundle()

        //register as just user - type 2
        binding?.btnRegistrationUser?.setOnClickListener {
            type = 2
            bundle.putInt("type", type)
            Log.e("main" , "user $type")
            Navigation.findNavController(view).navigate(R.id.action_registrationMainFragment_to_inputPhoneNumberFragment, bundle)
        }

        //register as owner
        binding?.btnRegistrationOwner?.setOnClickListener {
            type = 1
            bundle.putInt("type", type)
            Log.e("main" ,  " owner $type")
            Navigation.findNavController(view).navigate(R.id.action_registrationMainFragment_to_inputPhoneNumberFragment, bundle)
        }
        binding?.tvGoToMain?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_registrationMainFragment_to_playgroundListFragment)
        }

    }

}