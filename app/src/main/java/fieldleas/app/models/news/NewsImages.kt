package fieldleas.app.models.news


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NewsImages(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?
): Serializable