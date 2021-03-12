package fieldleas.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.fields_item.view.*
import com.example.fieldleasingpublic.R
import fieldleas.app.models.fields.FieldListItem


class FieldsAdapter(
        private val listener: OnItemClickListener, val context: Context
) :
        RecyclerView.Adapter<FieldsAdapter.FieldsHolder>(){

    private var fieldItem = emptyList<FieldListItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldsHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.fields_item, parent, false)

        return FieldsHolder(itemView)
    }

    override fun onBindViewHolder(holder: FieldsHolder, position: Int) {
        val currentItem = fieldItem[position]

        holder.itemName.text = currentItem.name
        holder.itemPrice.text = currentItem.price.substringBefore(".")

        holder.itemRating.rating = currentItem.rating

        if (currentItem.images.isNotEmpty()) {
            Glide.with(context)
                .load(currentItem.images[0].image)
                .dontAnimate()
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .into(holder.itemImage);
        }

        // add description and address
        holder.itemAddress.text = currentItem.location
        holder.itemDescription.text = currentItem.description


    }

    override fun getItemCount() = fieldItem.size

    internal fun setFieldsList(basketProducts: MutableList<FieldListItem>) {
        this.fieldItem = basketProducts
    }

    fun getItemAt(position: Int): FieldListItem {
        return fieldItem[position]
    }


    //**********************************************************************************************

    inner class FieldsHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

        val itemName: TextView = itemView.fields_fragment_stadium_name
        val itemImage: ImageView = itemView.fields_fragment_stadium_image
        val itemAddress: TextView = itemView.fields_fragment_stadium_address
        val itemPrice: TextView = itemView.fields_fragment_stadium_price
        val itemRating: RatingBar = itemView.fields_fragment_stadium_rating
        val itemDescription: TextView = itemView.fields_fragment_stadium_description


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(this.adapterPosition,v)
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int,v: View?)
    }

}