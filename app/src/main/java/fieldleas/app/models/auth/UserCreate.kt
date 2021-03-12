package fieldleas.app.models.auth

import com.google.gson.annotations.SerializedName

data class UserCreate(
    @SerializedName("phone_number") var phone_number: String,
    @SerializedName("full_name") var full_name: String,
    @SerializedName("type") var type: Int
)
