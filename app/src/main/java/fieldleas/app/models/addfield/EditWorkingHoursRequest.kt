package fieldleas.app.models.addfield

import com.google.gson.annotations.SerializedName

data class EditWorkingHoursRequest(
 /*   @SerializedName("id")
    val id: Int,*/
    @SerializedName("day")
    val day: String,
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String
)