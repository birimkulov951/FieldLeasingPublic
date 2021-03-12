package fieldleas.app.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import fieldleas.app.ui.main.FeedbacksFragment
import fieldleas.app.ui.main.FieldDetailsFragment

class SectionViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getPageTitle(position: Int): CharSequence? {
        super.getPageTitle(position)
        return when(position) {
            0 -> {
                "Описание"
            }
            else -> {
                "Отзывы"
            }
        }
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                FieldDetailsFragment()
            }
            else -> {
                FeedbacksFragment()
            }
        }

    }

    override fun getCount(): Int {
        return 2
    }

}