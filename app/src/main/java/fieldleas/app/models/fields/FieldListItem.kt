package fieldleas.app.models.fields


import com.google.gson.annotations.SerializedName

data class FieldListItem(
    @SerializedName("description")
    val description: String,
    @SerializedName("field_type")
    val fieldType: Int,
    @SerializedName("has_equipment")
    val hasEquipment: Boolean,
    @SerializedName("has_lights")
    val hasLights: Boolean,
    @SerializedName("has_locker_rooms")
    val hasLockerRooms: Boolean,
    @SerializedName("has_parking")
    val hasParking: Boolean,
    @SerializedName("has_rostrum")
    val hasRostrum: Boolean,
    @SerializedName("has_showers")
    val hasShowers: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("images")
    val images: List<Image>,
    @SerializedName("is_approved")
    val isApproved: Boolean,
    @SerializedName("is_indoor")
    val isIndoor: Boolean,
    @SerializedName("location")
    val location: String,
    @SerializedName("maximum_size")
    val maximumSize: Int,
    @SerializedName("minimum_size")
    val minimumSize: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("number_of_bookings")
    val numberOfBookings: Int,
    @SerializedName("number_of_players")
    val numberOfPlayers: Int,
    @SerializedName("owner")
    val owner: Int,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("working_hours")
    val workingHours: List<WorkingHour>,
    @SerializedName("disable_booking")
    var disableBooking: Boolean,
    @SerializedName("is_hidden")
    var isHidden: Boolean,
    @SerializedName("is_blacklisted")
    var isBlacklisted: Boolean
)