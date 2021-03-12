package fieldleas.app.models.auth

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("phone_number") val phone_number: String,
    @SerializedName("token") var token: String,
    @SerializedName("type") var type: Int,
    @SerializedName("full_name") var full_name: String)