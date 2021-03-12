package fieldleas.app.models.fields

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Image (
    @SerializedName("field")
    val field: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String
) : Serializable