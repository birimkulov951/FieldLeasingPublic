package fieldleas.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fieldleasingpublic.R
import kotlinx.android.synthetic.main.blacklist_item.view.*
import fieldleas.app.models.blacklist.BlackListResponse

class BlackListAdapter(
        val listener: OnItemClickListener, var users: List<BlackListResponse>)
    : RecyclerView.Adapter<BlackListAdapter.BlackListHolder>(){

    interface OnItemClickListener{
        fun unblockUser(position: Int, name: String, id: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlackListHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.blacklist_item, parent, false)

        return BlackListHolder(itemView)
    }

    override fun onBindViewHolder(holder: BlackListHolder, position: Int) {
        val currentUser = users[position]
        holder.name.text = currentUser.userName
        holder.unblockButton.setOnClickListener{
            if (listener != null){
                if (position != RecyclerView.NO_POSITION){
                    listener.unblockUser(position, currentUser.userName, currentUser.id)
                }
            }
        }
    }

    override fun getItemCount() = users.size

    inner class BlackListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.item_black_list
        val unblockButton = itemView.blackList_unblock_button
    }

}