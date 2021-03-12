package fieldleas.app.models.news


import com.google.gson.annotations.SerializedName

data class NewsListResponseItem(
        @SerializedName("body")
    val body: String?,
        @SerializedName("created_at")
    val createdAt: String?,
        @SerializedName("date")
    val date: String?,
        @SerializedName("id")
    val id: Int?,
        @SerializedName("images")
    val newsImages: List<NewsImages>?,
        @SerializedName("preview")
    val preview: String?,
        @SerializedName("time")
    val time: String?,
        @SerializedName("title")
    val title: String?
)