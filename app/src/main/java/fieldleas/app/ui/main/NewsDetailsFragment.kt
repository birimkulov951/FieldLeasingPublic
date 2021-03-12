package fieldleas.app.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import kotlinx.android.synthetic.main.fragment_news_details.view.*
import com.example.fieldleasingpublic.R
import fieldleas.app.adapters.ImageSliderAdapter
import fieldleas.app.adapters.ZoomOutPageTransformer
import fieldleas.app.models.news.NewsImages

@Suppress("UNCHECKED_CAST")
class NewsDetailsFragment : Fragment() {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView: View = inflater.inflate(R.layout.fragment_news_details, container, false)

        // Back button
        rootView.news_details_fragment_back_button.setOnClickListener{
            navController.navigate(R.id.action_newsDetailsFragment_to_newsFragment)
        }


        val newsTitle = requireArguments().getString("NEWS_TITLE_DATA", getString(R.string.did_not_defined))
        val newsImage = requireArguments().getSerializable("NEWS_IMAGE_DATA") as List<NewsImages>
        val newsDate  = requireArguments().getString("NEWS_DATE_DATA", getString(R.string.did_not_defined))
        val itemTime = requireArguments().getString("NEWS_TIME_DATA", getString(R.string.did_not_defined))
        val newsDescription = requireArguments().getString("NEWS_DESCRIPTION_DATA", getString(R.string.did_not_defined))

        rootView.news_details_fragment_news_title.text = newsTitle
        rootView.news_details_fragment_news_date.text = newsDate
        rootView.news_details_fragment_news_time.text = itemTime
        rootView.news_details_fragment_news_description.text = newsDescription

        val list = ArrayList<String>()
        for (element in newsImage) {
            list.add(element.image!!)
        }

        /** Setting sliding image adapter*/
        val dotsIndicator = rootView.findViewById<SpringDotsIndicator>(R.id.news_fragment_dots_indicator)
        val viewPager2 = rootView.findViewById<ViewPager2>(R.id.news_details_fragment_view_pager_2)
        val imageSliderAdapter = ImageSliderAdapter(list,requireContext())
        viewPager2.adapter = imageSliderAdapter

        val zoomOutPageTransformer = ZoomOutPageTransformer()
        viewPager2.setPageTransformer { page, position ->
            zoomOutPageTransformer.transformPage(page, position)
        }
        dotsIndicator.setViewPager2(viewPager2)

        /*Glide.with(this)
                   .load(newsImage)
                   .centerCrop()
                   .placeholder(R.drawable.placeholder_image)
                   .transition(DrawableTransitionOptions.withCrossFade(400))
                   .into(rootView.news_details_fragment_news_image);*/


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }


}