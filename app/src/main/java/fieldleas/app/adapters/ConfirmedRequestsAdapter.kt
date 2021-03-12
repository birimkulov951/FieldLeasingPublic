package fieldleas.app.adapters
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.request_confirmed_item.view.*
import com.example.fieldleasingpublic.R
import fieldleas.app.models.booking.BookingsResponseItem

class ConfirmedRequestsAdapter (
        val context: Context, var bookingItems: List<BookingsResponseItem>)
    : RecyclerView.Adapter<ConfirmedRequestsAdapter.ConfirmedRequestsHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmedRequestsHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.request_confirmed_item, parent, false)
        return ConfirmedRequestsHolder(itemView)
    }


    override fun getItemCount() = bookingItems.size


    //**********************************************************************************************

    inner class ConfirmedRequestsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val name: TextView = itemView.requests_user_name
        val field = itemView.requests_field_name
        val bookingDate: TextView = itemView.request_date
        val bookingTime: TextView = itemView.request_time
        val number = itemView.requests_phone_number


    }

    override fun onBindViewHolder(holder: ConfirmedRequestsAdapter.ConfirmedRequestsHolder, position: Int) {
        val currentItem = bookingItems[position]
        holder.name.text = currentItem.user!!.fullName
        holder.field.text = currentItem.field!!.name
        holder.bookingDate.text = currentItem.bookingDate
        val start = currentItem.timeStart!!.subSequence(0,5)
        val end = currentItem.timeEnd!!.subSequence(0,5)
        holder.bookingTime.text = "$start-$end"
        holder.number.text = currentItem.user.phoneNumber

        holder.number.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${currentItem.user.phoneNumber}"))
            context.startActivity(intent)
        }

    }

}
