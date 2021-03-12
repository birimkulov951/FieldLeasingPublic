package fieldleas.app.models.fields


import com.google.gson.annotations.SerializedName

data class FieldReviewsItem(
    @SerializedName("description")
    val description: String,
    @SerializedName("field")
    val `field`: Int,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("rate")
    val rate: Float,
    @SerializedName("review_date")
    val reviewDate: String,
    @SerializedName("user")
    val user: Int
)