package fieldleas.app.models.booking


import com.google.gson.annotations.SerializedName

data class Field(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)