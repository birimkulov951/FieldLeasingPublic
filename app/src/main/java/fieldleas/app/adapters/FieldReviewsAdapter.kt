package fieldleas.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.reviews_item.view.*
import com.example.fieldleasingpublic.R
import fieldleas.app.models.fields.FieldReviewsItem


class FieldReviewsAdapter( val context: Context) : RecyclerView.Adapter<FieldReviewsAdapter.ReviewsViewHolder>(){


    private var item = emptyList<FieldReviewsItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.reviews_item, parent, false)

        return ReviewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        val currentItem = item[position]

        holder.itemName.text = currentItem.fullName
        holder.itemRating.rating = currentItem.rate
        holder.feedback.text = currentItem.description
        holder.date.text = currentItem.reviewDate

    }


    override fun getItemCount() = item.size

    internal fun setReviewsList(itemsList: List<FieldReviewsItem>) {
        this.item = itemsList
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): FieldReviewsItem {
        return item[position]
    }


    //**********************************************************************************************

    class ReviewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val itemName: TextView = itemView.field_reviews_fragment_name_text
        val itemRating: RatingBar = itemView.field_reviews_fragment_rating_bar
        val feedback: TextView = itemView.field_reviews_fragment_feedback_text
        val date: TextView = itemView.field_reviews_fragment_feedback_date


    }


}