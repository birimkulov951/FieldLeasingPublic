package fieldleas.app.models.fields.userfields


import com.google.gson.annotations.SerializedName

data class WorkingHour(
    @SerializedName("day")
    val day: String?,
    @SerializedName("end")
    val end: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("start")
    val start: String?
)