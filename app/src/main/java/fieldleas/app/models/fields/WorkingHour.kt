package fieldleas.app.models.fields


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WorkingHour(
    @SerializedName("day")
    val day: String,
    @SerializedName("end")
    val end: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("start")
    val start: String
) : Serializable