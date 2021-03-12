package fieldleas.app.models.booking


import com.google.gson.annotations.SerializedName

data class BookingsResponseItem(
    @SerializedName("booking_date")
    val bookingDate: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("field")
    val `field`: Field?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("is_finished")
    val isFinished: Boolean?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("time_end")
    val timeEnd: String?,
    @SerializedName("time_start")
    val timeStart: String?,
    @SerializedName("user")
    val user: User?,
    @SerializedName("feedback_given")
    val isFeedbackGiven: Boolean?,

)