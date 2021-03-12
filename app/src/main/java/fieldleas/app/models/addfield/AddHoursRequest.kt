package fieldleas.app.models.addfield


import com.google.gson.annotations.SerializedName

data class AddHoursRequest(
    @SerializedName("field")
    val field: Int?,
    @SerializedName("working_hours")
    val workingHours: List<WorkingHourX>?
)