package fieldleas.app.models.booking


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("phone_number")
    val phoneNumber: String?
)