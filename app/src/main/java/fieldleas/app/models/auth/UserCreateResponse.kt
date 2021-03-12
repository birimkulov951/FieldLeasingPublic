package fieldleas.app.models.auth

import com.google.gson.annotations.SerializedName

data class UserCreateResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("phone_number") val phone_number: String,
    @SerializedName("full_name") val full_name: String,
    @SerializedName("type") val type : Int
)