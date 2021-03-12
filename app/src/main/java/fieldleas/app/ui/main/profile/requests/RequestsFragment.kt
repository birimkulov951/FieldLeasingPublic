package fieldleas.app.ui.main.profile.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.fieldleasingpublic.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_requests_view_pager.view.*
import fieldleas.app.adapters.RequestsViewPagerAdapter

class RequestsFragment : Fragment() {

    private val TAG = "RequestsFragment"
    private lateinit var navController: NavController
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.fragment_requests_view_pager, container, false)
        rootView.requests_view_pager.adapter = RequestsViewPagerAdapter(fragment.childFragmentManager)
        rootView.requests_tab_layout.setupWithViewPager(rootView.requests_view_pager)
        rootView.requests_back_button.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return rootView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }
}