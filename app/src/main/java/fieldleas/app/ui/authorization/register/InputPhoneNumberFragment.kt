package fieldleas.app.ui.authorization.register

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_input_phone_number.*
import com.example.fieldleasingpublic.R
import com.example.fieldleasingpublic.databinding.FragmentInputPhoneNumberBinding

class InputPhoneNumberFragment : Fragment() {

    private lateinit var navController: NavController

    var type = -1
    private var _binding: FragmentInputPhoneNumberBinding? = null
    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentInputPhoneNumberBinding.inflate(inflater, container, false)
        binding?.tvGoToMain?.setOnClickListener {
            navController.navigate(R.id.action_inputPhoneNumberFragment_to_playgroundListFragment)
        }

        binding?.editInputPhoneNumber?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!edit_inputPhoneNumber?.text.toString().startsWith("+996")){
                    edit_inputPhoneNumber?.setText("+996")
                    Selection.setSelection(edit_inputPhoneNumber.text, edit_inputPhoneNumber.text.length);
                }
                if (edit_inputPhoneNumber.text.length == 13) {
                    binding?.btnSendCode?.setBackgroundResource(R.drawable.background_rectangle_green)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        binding?.btnSendCode?.setOnClickListener {
            checkDataEntered(it)

        }

        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }


    private fun checkDataEntered(view: View) = if (isEmpty(edit_inputPhoneNumber)) {
        edit_inputPhoneNumber.error = getString(R.string.field_should_be_filled)
    }
    else if(edit_inputPhoneNumber.text.length < 13){
        edit_inputPhoneNumber.error = getString(R.string.number_format)
    }
    else if(!edit_inputPhoneNumber.text.toString().startsWith("+996")) {
        edit_inputPhoneNumber.error = getString(R.string.number_format)
    }
    else{
        //save phoneNumber to bundle to pass to the next fragment
        val bundle = this.arguments
        val data = Bundle()
        data?.putString("phone_number", edit_inputPhoneNumber.text.toString())
        arguments?.getInt("type")?.let {data?.putInt("type", it) }
        Log.e("bundle", bundle?.getInt("type").toString())
        hideKeyBoard(view)
        navController.navigate(R.id.action_inputPhoneNumberFragment_to_confirmAccountFragment, data)

    }

    private fun hideKeyBoard(rootView: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
    }

    private fun isEmpty(text: EditText): Boolean {
        val str: CharSequence = text.text.toString().trim()
        return TextUtils.isEmpty(str)

    }
    }

