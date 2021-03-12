package fieldleas.app.models.blacklist

import com.google.gson.annotations.SerializedName

data class BlackListCreate(
    @SerializedName("field") var field_id: Int,
    @SerializedName("user_id") var user: Int
)