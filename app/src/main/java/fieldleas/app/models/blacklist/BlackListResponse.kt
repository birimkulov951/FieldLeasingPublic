package fieldleas.app.models.blacklist

import com.google.gson.annotations.SerializedName

data class BlackListResponse(
    @SerializedName("id") var id: Int,
    @SerializedName("field") var field_id: Int,
    @SerializedName("user") var userName: String,
    @SerializedName("data_of_ban") var date: String
)