package fieldleas.app.adapters
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.request_new_item.view.*
import com.example.fieldleasingpublic.R
import fieldleas.app.models.booking.BookingsResponseItem

class NewRequestsAdapter(
    val context: Context,
    var listener: OnItemClickListener, var bookingItems: List<BookingsResponseItem>)
    : RecyclerView.Adapter<NewRequestsAdapter.NewRequestsHolder>(){
    interface OnItemClickListener{
        fun onItemAccept(position: Int, id: Int, currentItem: BookingsResponseItem)
        fun onItemDeny(position: Int, id: Int, currentItem: BookingsResponseItem)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewRequestsHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.request_new_item, parent, false)
        return NewRequestsHolder(itemView)
    }


    override fun getItemCount() = bookingItems.size


    inner class NewRequestsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val name: TextView = itemView.requests_user_name
        val field = itemView.requests_field_name
        val bookingDate: TextView = itemView.request_date
        val bookingTime: TextView = itemView.request_time
        val number = itemView.requests_phone_number
        val accept: Button = itemView.requests_accept_button
        val deny = itemView.requests_deny_button


    }

    override fun onBindViewHolder(holder: NewRequestsAdapter.NewRequestsHolder, position: Int) {
            val currentItem = bookingItems[position]
            holder.name.text = currentItem.user!!.fullName
            holder.field.text = currentItem.field!!.name
            holder.number.text = currentItem.user.phoneNumber
            holder.bookingDate.text = currentItem.bookingDate
            var start = currentItem.timeStart!!.subSequence(0,5)
            var end = currentItem.timeEnd!!.subSequence(0,5)
            holder.bookingTime.text = "$start-$end"
        holder.deny.setOnClickListener {
            if (listener != null){
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemDeny(position, currentItem.id!!, currentItem)
                }
            }
        }
        holder.accept.setOnClickListener {
            if (listener != null){
                if (position != RecyclerView.NO_POSITION){
                    listener.onItemAccept(position, currentItem.id!!, currentItem)
                }
            }
        }
        holder.number.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${currentItem.user.phoneNumber}"))
            context.startActivity(intent)
        }



    }

    }
