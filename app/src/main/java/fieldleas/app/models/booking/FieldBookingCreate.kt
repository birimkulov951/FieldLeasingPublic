package fieldleas.app.models.booking

import com.google.gson.annotations.SerializedName

data class FieldBookingCreate (
        @SerializedName("field")
        val field: Int,
        @SerializedName("booking_date")
        val booking_date: String,
        @SerializedName("time_start")
        val time_start: String,
        @SerializedName("time_end")
        val time_end: String,
        @SerializedName("status")
        val status: Int
)