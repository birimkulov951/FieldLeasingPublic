package fieldleas.app.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_view_pager.view.*
import fieldleas.app.MainActivity
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.SectionViewPagerAdapter
import fieldleas.app.models.fields.Image
import fieldleas.app.models.fields.WorkingHour
import fieldleas.app.network.SessionManager

class ViewPagerFragment : Fragment() {

    private val TAG = "ViewPagerFragment"
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager

    /** Helper vars*/
    var fieldId = -1
    var fieldType = -1
    var owner = -1
    var phoneNumber = ""
    var name = ""
    var price = ""
    var location = ""
    var description = ""
    var isApproved = false
    var numberOfPlayers = -1
    var hasParking = false
    var isIndoor = false
    var hasShowers = false
    var hasLockerRooms = false
    var hasLights = false
    var hasRostrum = false
    var hasEquipment = false
    var minimumSize = -1
    var maximumSize = -1
    var images = emptyList<Image>()
    var rating = -1.0f
    var numberOfBookings = -1
    var workingHours = emptyList<WorkingHour>()
    var disableBooking = false
    var isDataEmpty = false

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        val rootView: View =  inflater.inflate(R.layout.fragment_view_pager, container, false)
        sessionManager = SessionManager(requireContext())


        fieldId = requireArguments().getInt("DATA_ID",-1)
        isDataEmpty = requireArguments().getBoolean("DATA_IS_EMPTY",false)
        name = requireArguments().getString("DATA_NAME","null")

        if (isDataEmpty) {
            images = emptyList<Image>()
            workingHours = emptyList<WorkingHour>()
        } else {
            images = requireArguments().getSerializable("DATA_IMAGES") as List<Image>
            workingHours = requireArguments().getSerializable("DATA_WORKING_HOURS") as List<WorkingHour>
        }

        fieldType = requireArguments().getInt("DATA_FIELD_TYPE",-1)
        owner = requireArguments().getInt("DATA_OWNER",-1)
        phoneNumber = requireArguments().getString("DATA_PHONE_NUMBER","null")
        price = requireArguments().getString("DATA_PRICE","null")
        location = requireArguments().getString("DATA_LOCATION","null")
        description = requireArguments().getString("DATA_DESCRIPTION","null")
        isApproved = requireArguments().getBoolean("DATA_IS_APPROVED",false)
        numberOfPlayers = requireArguments().getInt("DATA_NUMBER_OF_PLAYERS",-1)
        hasParking = requireArguments().getBoolean("DATA_HAS_PARKING",false)
        isIndoor = requireArguments().getBoolean("DATA_IS_INDOOR",false)
        hasShowers = requireArguments().getBoolean("DATA_HAS_SHOWERS",false)
        hasLockerRooms = requireArguments().getBoolean("DATA_HAS_LOCKER_ROOMS",false)
        hasLights = requireArguments().getBoolean("DATA_HAS_LIGHTS",false)
        hasRostrum = requireArguments().getBoolean("DATA_HAS_ROSTRUM",false)
        hasEquipment = requireArguments().getBoolean("DATA_HAS_EQUIPMENT",false)
        minimumSize = requireArguments().getInt("DATA_MINIMUM_SIZE",-1)
        maximumSize = requireArguments().getInt("DATA_MAXIMUM_SIZE",-1)
        rating = requireArguments().getFloat("DATA_RATING",-1.0f)
        numberOfBookings = requireArguments().getInt("DATA_NUMBER_OF_BOOKINGS",-1)
        disableBooking = requireArguments().getBoolean("DATA_DISABLE_BOOKING", false)


        /** Handling ViewPager */
        rootView.view_pager_fragment_view_pager.adapter = SectionViewPagerAdapter(fragment.childFragmentManager)
        rootView.view_pager_fragment_tab_layout.setupWithViewPager(rootView.view_pager_fragment_view_pager)

        /** ViewPagerFragment Label */
        rootView.view_pager_fragment_toolbar_text.text = name

        /** Back Button */
        rootView.view_pager_fragment_back_button.setOnClickListener{
            (activity as MainActivity).onBackPressed()
        }


        return rootView
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

}