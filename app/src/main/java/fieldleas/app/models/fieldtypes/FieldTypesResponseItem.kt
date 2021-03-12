package fieldleas.app.models.fieldtypes


import com.google.gson.annotations.SerializedName

data class FieldTypesResponseItem(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)