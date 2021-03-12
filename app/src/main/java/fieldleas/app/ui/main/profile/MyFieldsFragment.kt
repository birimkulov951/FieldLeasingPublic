package fieldleas.app.ui.main.profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.custom_dialog_2.view.*
import kotlinx.android.synthetic.main.custom_dialog_3.view.*
import kotlinx.android.synthetic.main.custom_dialog_3.view.custom_dialog_ok_button
import kotlinx.android.synthetic.main.fragment_my_fields.*
import kotlinx.android.synthetic.main.fragment_my_fields.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.MyFieldsAdapter
import fieldleas.app.models.fields.userfields.UserFieldsResponseItem
import fieldleas.app.network.ApiService
import fieldleas.app.network.RetrofitInstance
import fieldleas.app.network.SessionManager
import fieldleas.app.utils.Constants.IF_EDIT_FRAGMENT

class MyFieldsFragment : Fragment(), MyFieldsAdapter.OnItemClickListener {

    private val TAG = "MyFieldsFragment"

    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager
    private lateinit var myFieldsAdapter: MyFieldsAdapter
    private lateinit var builder2: AlertDialog
    private var myFieldsList = ArrayList<UserFieldsResponseItem>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val rootView: View =  inflater.inflate(R.layout.fragment_my_fields, container, false)

        sessionManager = SessionManager(requireContext())

        myFieldsAdapter = MyFieldsAdapter( this@MyFieldsFragment, requireContext())
        rootView.my_fields_fragment_recycler_view.adapter = myFieldsAdapter

        rootView.fields_fragment_progressbar.visibility = View.VISIBLE

        getUserFields()

        return rootView
    }

    private fun getUserFields() {

        val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        val call = retroInstance.getUserFields("Bearer ${sessionManager.fetchAuthToken()!!}")

        call.enqueue(object : Callback<MutableList<UserFieldsResponseItem>> {

            override fun onResponse(call: Call<MutableList<UserFieldsResponseItem>>, response: Response<MutableList<UserFieldsResponseItem>>) {

                if (response.isSuccessful) {

                        fields_fragment_progressbar.visibility = View.GONE

                    if (response.body()!!.isEmpty()) {

                        fields_fragment_empty_text.visibility = View.VISIBLE

                    } else {
                        // Handling RecycleView and it's animation
                        val lac: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_fall_down)

                        myFieldsList = response.body() as ArrayList<UserFieldsResponseItem>
                        myFieldsAdapter.setItemList(myFieldsList)

                        // Categories RecycleView has fixed size. LayoutManager is written in XML file
                        my_fields_fragment_recycler_view.layoutAnimation = lac
                        my_fields_fragment_recycler_view.startLayoutAnimation()
                    }

                    Log.e(TAG, "onResponse: ${response.body()}")

                } else {
                    Log.e(TAG, "onResponse: ${response.body()}")
                    Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MutableList<UserFieldsResponseItem>>, t: Throwable) {
                Log.e(TAG, "onFailure: $t")
                Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onItemClick(v: View?, position: Int) {
        val clickedItem: UserFieldsResponseItem = myFieldsAdapter.getItemAt(position)

        val bundle = Bundle()
        bundle.putInt("DATA_ID",clickedItem.id)

        Log.d(TAG, "onItemClick: item clicked")
    }

    override fun onEditClick(v: View?, position: Int) {
        val clickedItem: UserFieldsResponseItem = myFieldsAdapter.getItemAt(position)
        // open Field redactor fragment and send bundle with field id.
        val sender = Bundle()
        sender.putInt(IF_EDIT_FRAGMENT, clickedItem.id)

        navController.navigate(R.id.action_myFieldsFragment_to_addFieldFragment,sender)
    }

    override fun onDeleteClick(v: View?, position: Int) {
        val clickedItem: UserFieldsResponseItem = myFieldsAdapter.getItemAt(position)

        val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_2,null)
        val builder = AlertDialog.Builder(context).setView(alertDialog).show()
        alertDialog.custom_dialog_2_alert_title.text = getString(R.string.delete_field_msg)

        alertDialog.custom_dialog_2_btn_cancel.setOnClickListener {
            builder.dismiss()
        }

        alertDialog.custom_dialog_2_btn_ok.setOnClickListener {
            builder.dismiss()
            val alertDialog2 = LayoutInflater.from(context).inflate(R.layout.custom_dialog,null)
            builder2= AlertDialog.Builder(context).setView(alertDialog2).show()
            builder2.setCanceledOnTouchOutside(false)
            deleteField(clickedItem.id)

        }

    }

    private fun deleteField(fieldId: Int) {

        val retroInstance = RetrofitInstance.getRetrofitInstance().create(ApiService::class.java)

        val call = retroInstance.deleteField(fieldId)

        call.enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                if (response.isSuccessful) {
                    builder2.dismiss()

                    myFieldsList.clear()
                    myFieldsAdapter.notifyDataSetChanged()
                    getUserFields()

                    val alertDialog = LayoutInflater.from(context).inflate(R.layout.custom_dialog_3,null)
                    val builder = AlertDialog.Builder(context).setView(alertDialog).show()
                    alertDialog.custom_dialog_alert_title.text = getString(R.string.successfully_deleted)
                    alertDialog.custom_dialog_ok_button.setOnClickListener {
                        builder.dismiss()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

}