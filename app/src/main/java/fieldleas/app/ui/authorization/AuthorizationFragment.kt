package fieldleas.app.ui.authorization

import android.content.Context
import kotlinx.android.synthetic.main.fragment_authorization.*
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.navigation.Navigation
import com.example.fieldleasingpublic.R
import com.example.fieldleasingpublic.databinding.FragmentAuthorizationBinding


class AuthorizationFragment : Fragment() {
    private var _binding: FragmentAuthorizationBinding? = null
    private val binding get() = _binding
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.authorizationTvGoToMain?.setOnClickListener {
            view?.let { Navigation.findNavController(it).navigate(R.id.action_authorizationFragment2_to_playgroundListFragment) }
        }

        authorization_inputPhoneNumber?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!authorization_inputPhoneNumber?.text.toString().startsWith("+996")){
                    authorization_inputPhoneNumber?.setText("+996")
                    Selection.setSelection(authorization_inputPhoneNumber.text, authorization_inputPhoneNumber.text.length)
                }
                if (authorization_inputPhoneNumber.text.length == 13) {
                    authorization_btn_sendCode?.setBackgroundResource(R.drawable.background_rectangle_green)
                    authorization_btn_sendCode?.setOnClickListener {
                        checkDataEntered(it)
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

    }

    private fun checkDataEntered(view: View) {
        if (isEmpty(authorization_inputPhoneNumber)) {
            authorization_inputPhoneNumber.error = getString(R.string.field_should_be_filled)
        }
        else if(!authorization_inputPhoneNumber.text.toString().startsWith("+996")) {
            authorization_inputPhoneNumber.error = getString(R.string.number_format)
        }
        else {
            startPhoneNumberVerification(authorization_inputPhoneNumber.text.toString(), view)
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String, rootView: View) {
        val bundle = Bundle()
        bundle.putString("phoneNumber", phoneNumber)
        hideKeyBoard(rootView)
        view?.let { Navigation.findNavController(it).navigate(R.id.action_authorizationFragment2_to_authorizationConfirmAccountFragment, bundle) }
    }

    private fun hideKeyBoard(rootView: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
    }

    //if input fields are empty
    private fun isEmpty(text: EditText): Boolean {
        val str: CharSequence = text.text.toString().trim()
        return TextUtils.isEmpty(str)

    }

}