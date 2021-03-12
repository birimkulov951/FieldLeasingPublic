package fieldleas.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.news_item.view.*
import com.example.fieldleasingpublic.R
import fieldleas.app.models.news.NewsListResponseItem

class NewsAdapter(
    private val listener: OnItemClickListener, val context: Context
) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){


    private var basketItem = emptyList<NewsListResponseItem>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item, parent, false)

        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = basketItem[position]

        holder.itemTitle.text = currentItem.title
        holder.itemDate.text = currentItem.date
        holder.itemTime.text = currentItem.time

        Glide.with(context)
            .load(currentItem.preview)
            .dontAnimate()
            .transition(DrawableTransitionOptions.withCrossFade(400))
            .centerCrop()
            .into(holder.itemImage);

       /* if (currentItem.newsImages!!.isNotEmpty()) {
            Glide.with(context)
                    .load(currentItem.newsImages[0].image)
                    .placeholder(R.drawable.placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade(400))
                    .centerCrop()
                    .into(holder.itemImage);
        }*/
    }

    override fun getItemCount() = basketItem.size

    internal fun setNewsList(basketProducts: MutableList<NewsListResponseItem>) {
        this.basketItem = basketProducts
        notifyDataSetChanged()
    }

    fun getProductAt(position: Int): NewsListResponseItem {
        return basketItem[position]
    }


    //**********************************************************************************************

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val itemTitle: TextView = itemView.news_fragment_text_text
        val itemImage: ImageView = itemView.news_fragment_news_image
        val itemDate: TextView = itemView.news_fragment_date_text
        val itemTime: TextView = itemView.news_fragment_time_text


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