package fieldleas.app.models.booking.requests

import com.google.gson.annotations.SerializedName

data class RequestStatus (
    @SerializedName("field")
    var field:Int,
    @SerializedName("status")
    var status: Int
)