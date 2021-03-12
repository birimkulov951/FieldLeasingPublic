package fieldleas.app.models.addfield


import com.google.gson.annotations.SerializedName

data class WorkingHourX(
    @SerializedName("day")
    val day: String,
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String

)