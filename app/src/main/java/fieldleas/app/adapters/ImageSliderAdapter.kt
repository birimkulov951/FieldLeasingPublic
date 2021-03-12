package fieldleas.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.fieldleasingpublic.R

class ImageSliderAdapter(private val slides : List<String>,  val context: Context)
    : RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>(){

    inner class ImageSliderViewHolder(view : View) : RecyclerView.ViewHolder(view){
        private val image = view.findViewById<ImageView>(R.id.image_slide)

        fun bind(slider : String){

            Glide.with(context)
                .load(slider)
                .dontAnimate()
                .centerCrop()
                //.place(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .into(image);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
        return ImageSliderViewHolder(LayoutInflater.from(parent.context).
        inflate(R.layout.slide_image_container, parent, false))
    }

    override fun getItemCount(): Int {
        return slides.size
    }

    override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {
        holder.bind(slides[position])
    }
}