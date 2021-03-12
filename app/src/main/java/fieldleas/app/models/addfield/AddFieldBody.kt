package fieldleas.app.models.addfield


import com.google.gson.annotations.SerializedName

data class AddFieldBody(
    @SerializedName("description")
    val description: String?,
    @SerializedName("field_type")
    val fieldType: Int?,
    @SerializedName("has_equipment")
    val hasEquipment: Boolean?,
    @SerializedName("has_lights")
    val hasLights: Boolean?,
    @SerializedName("has_locker_rooms")
    val hasLockerRooms: Boolean?,
    @SerializedName("has_parking")
    val hasParking: Boolean?,
    @SerializedName("has_rostrum")
    val hasRostrum: Boolean?,
    @SerializedName("has_showers")
    val hasShowers: Boolean?,
    @SerializedName("images")
    val images: List<String>?,
    @SerializedName("is_approved")
    val isApproved: Boolean?,
    @SerializedName("is_indoor")
    val isIndoor: Boolean?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("maximum_size")
    val maximumSize: Int?,
    @SerializedName("minimum_size")
    val minimumSize: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("number_of_players")
    val numberOfPlayers: Int?,
    @SerializedName("price")
    val price: String?,
    @SerializedName("disable_booking")
    val disableBooking: Boolean
)