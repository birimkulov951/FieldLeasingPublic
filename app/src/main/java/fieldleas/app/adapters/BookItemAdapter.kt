package fieldleas.app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.book_item.view.*
import com.example.fieldleasingpublic.R
import fieldleas.app.models.booking.BookingsResponseItem
import java.text.SimpleDateFormat

class BookItemAdapter(var context: Context,
                      val listener: OnItemClickListener, var bookingItems: List<BookingsResponseItem>)
    : RecyclerView.Adapter<BookItemAdapter.BookItemHolder>(){


    private var hideButton = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_item, parent, false)
        return BookItemHolder(itemView)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: BookItemHolder, position: Int) {
        val currentItem = bookingItems[position]
        holder.name.text = currentItem.field!!.name
        holder.bookingDate.text = currentItem.bookingDate
        holder.status.text = currentItem.status

        if (currentItem.isFinished!! && currentItem.status == "Одобрено" && !currentItem.isFeedbackGiven!!) {
            holder.cancelBooking.text = context.getString(R.string.leave_feedback)
        } else {
            holder.cancelBooking.text = context.getString(R.string.cancel_book)
        }

        if (hideButton) {
            //holder.status.visibility = View.GONE
            holder.cancelBooking.visibility = View.GONE
        }
        val start  = currentItem.timeStart!!.subSequence(0,5)
        val end = currentItem.timeEnd!!.subSequence(0,5)
        holder.bookingTime.text = "$start-$end"

        holder.cancelBooking.setOnClickListener {
            if (position != RecyclerView.NO_POSITION){
                listener.onItemCancel(position, currentItem.id!!, currentItem, holder.itemView.rootView)
            }
        }

        Glide.with(context)
                .load(currentItem.image)
                .dontAnimate()
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .into(holder.image)

    }

    override fun getItemCount() = bookingItems.size

    fun getItemAt(position: Int): BookingsResponseItem {
        return bookingItems[position]
    }

    fun hideButton() {
        hideButton = true
    }

    //**********************************************************************************************

    inner class BookItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener{

        val name: TextView = itemView.book_item_playground_name
        val image: ImageView = itemView.book_item_playground_image_view
        val bookingDate: TextView = itemView.book_item_date
        val bookingTime: TextView = itemView.book_item_time
        val cancelBooking: Button = itemView.book_item_cancel_booking
        val status = itemView.book_item_status

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(this.adapterPosition,v)
        }
    }
    interface OnItemClickListener{
        fun onItemClick(position: Int,v: View?)
        fun onItemCancel(position: Int, id: Int, bookingItem: BookingsResponseItem, v: View)
    }
}