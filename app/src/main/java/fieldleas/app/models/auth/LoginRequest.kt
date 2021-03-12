package fieldleas.app.models.auth

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("phone_number") val phone_number: String
)