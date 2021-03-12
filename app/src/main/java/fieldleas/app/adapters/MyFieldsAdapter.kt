package fieldleas.app.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.my_fields_item.view.*
import com.example.fieldleasingpublic.R
import fieldleas.app.models.fields.userfields.UserFieldsResponseItem

class MyFieldsAdapter (
    private val listener: OnItemClickListener, val context: Context
) : RecyclerView.Adapter<MyFieldsAdapter.MyFieldsViewHolder>(){

    private var listItem = ArrayList<UserFieldsResponseItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFieldsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_fields_item, parent, false)

        return MyFieldsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyFieldsViewHolder, position: Int) {
        val currentItem = listItem[position]

        holder.itemTitle.text = currentItem.name

        if (currentItem.isApproved == true) {
            holder.itemStatus.text = context.getString(R.string.active_status)
            holder.itemStatus.setTextColor(Color.parseColor("#0D8549"))
        } else {
            holder.itemStatus.text = context.getString(R.string.hidden_status)
            holder.itemStatus.setTextColor(Color.parseColor("#FF0000"))
        }

        Glide.with(context)
            .load(currentItem.images!![0].image!!)
            .dontAnimate()
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade(400))
            .into(holder.itemImage)

        // Added button listeners
        holder.itemEdit.setOnClickListener{
            listener.onEditClick(holder.itemView.rootView,position)
        }
        holder.itemDelete.setOnClickListener{
            listener.onDeleteClick(holder.itemView.rootView,position)
        }


    }

    override fun getItemCount() = listItem.size

    internal fun setItemList(basketProducts: ArrayList<UserFieldsResponseItem>) {
        this.listItem.clear()
        this.listItem = basketProducts
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): UserFieldsResponseItem {
        return listItem[position]
    }


    //**********************************************************************************************

    inner class MyFieldsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val itemTitle: TextView = itemView.my_fields_fragment_text_name
        val itemImage: ImageView = itemView.my_fields_fragment_image
        val itemStatus: TextView = itemView.my_fields_fragment_text_status

        val itemEdit: TextView = itemView.my_fields_fragment_edit
        val itemDelete: TextView = itemView.my_fields_fragment_delete

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(v,adapterPosition)
        }

    }

    interface OnItemClickListener {
        fun onItemClick(v: View?,position: Int)
        fun onEditClick(v: View?,position: Int)
        fun onDeleteClick(v: View?, position: Int)
    }

}