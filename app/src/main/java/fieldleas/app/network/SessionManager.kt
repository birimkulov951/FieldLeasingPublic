package fieldleas.app.network

import android.content.Context
import android.content.SharedPreferences
import com.example.fieldleasingpublic.R
import fieldleas.app.utils.Constants.FILTER_BY_BOOKINGS

class SessionManager(context: Context) {

    private var preferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    private var tokenPrefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.ok), Context.MODE_PRIVATE)
    private var userInfo: SharedPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE)
    private var filterSettings: SharedPreferences = context.getSharedPreferences(FIELD_TYPE, Context.MODE_PRIVATE)


    companion object {

        // Tokens
        const val USER_ID = "user_id"
        const val USER_ACCESS_TOKEN = "user_access_token"
        const val USER_ACCESS_TOKEN_EXPIRATION_TIME = "user_access_token_expiration_time"
        const val USER_REFRESH_TOKEN = "user_refresh_token"
        const val USER_REFRESH_TOKEN_EXPIRATION_TIME = "user_refresh_token_expiration_time"

        //User
        const val USER_INFO = "user_info"
        const val USER_NAME = "user_name"
        const val USER_PHONE_NUMBER = "user_number"
        const val USER_TYPE = "user_type"

        //User bookings
        const val  USER_BOOKING = "user_booking"

        // Filter settings
        const val FIELD_TYPE = "field_type"
        const val START_PRICE = "start_price"
        const val END_PRICE = "end_price"
        const val MIN_PLAYERS = "min_players"
        const val MAX_PLAYERS = "max_players"
        const val DESIRED_DATE = "desired_date"
        const val DESIRED_TIME_START = "desired_time_start"
        const val DESIRED_TIME_END = "desired_time_end"
        const val RATING_FILTER = "rating_filter"
        const val HAS_PARKING = "has_parking"
        const val IS_INDOOR = "is_indoor"
        const val HAS_SHOWERS = "has_showers"
        const val HAS_LOCKER_ROOMS = "has_locker_rooms"
        const val HAS_LIGHTS = "has_lights"
        const val HAS_ROSTRUM = "has_rostrum"
        const val HAS_EQUIPMENT = "has_equipment"
        const val FILTER_HELPER_INT = "filter_helper_int"

    }

    /** Saving auth token */
    fun saveAuthTokens(
            userId: Int,
            token: String,
            accessTokenExpTime: String,
            refreshToken: String,
            refreshTokenExpTime: String) {
        val editor = tokenPrefs.edit()
        editor.putInt(USER_ID, userId)
        editor.putString(USER_ACCESS_TOKEN, token)
        editor.putString(USER_ACCESS_TOKEN_EXPIRATION_TIME, accessTokenExpTime)
        editor.putString(USER_REFRESH_TOKEN, refreshToken)
        editor.putString(USER_REFRESH_TOKEN_EXPIRATION_TIME, refreshTokenExpTime)
        editor.apply()
    }


    // only 1 token?
    fun saveAuthToken(token: String) {
        val editor = preferences.edit()
        editor.putString(USER_ACCESS_TOKEN, token)
        editor.apply()
    }
    fun fetchAuthToken(): String? {
        return preferences.getString(USER_ACCESS_TOKEN, null)
    }
    fun deleteOldToken(){
        val editor = preferences.edit()
        editor.clear().apply()
    }

    // user Info
    fun saveFilterSettings(fieldType: String, startPrice: Int, endPrice: Int, minPlayers : Int,
                     maxPlayers: Int, desiredDate: String, desiredTimeStart: String, desiredTimeEnd : String,
                     ratingFilter: String, hasParking: Boolean, isIndoor: Boolean, hasShowers : Boolean,
                     hasLockerRooms: Boolean, hasLights: Boolean, hasRostrum: Boolean, hasEquipment : Boolean, filterHelperInt: Int){
        val editor = filterSettings.edit()
        editor.putString(FIELD_TYPE, fieldType)
        editor.putInt(START_PRICE, startPrice)
        editor.putInt(END_PRICE, endPrice)
        editor.putInt(MIN_PLAYERS, minPlayers)
        editor.putInt(MAX_PLAYERS, maxPlayers)
        editor.putString(DESIRED_DATE, desiredDate)
        editor.putString(DESIRED_TIME_START, desiredTimeStart)
        editor.putString(DESIRED_TIME_END, desiredTimeEnd)
        editor.putString(RATING_FILTER, ratingFilter)
        editor.putBoolean(HAS_PARKING, hasParking)
        editor.putBoolean(IS_INDOOR, isIndoor)
        editor.putBoolean(HAS_SHOWERS, hasShowers)
        editor.putBoolean(HAS_LOCKER_ROOMS, hasLockerRooms)
        editor.putBoolean(HAS_LIGHTS, hasLights)
        editor.putBoolean(HAS_ROSTRUM, hasRostrum)
        editor.putBoolean(HAS_EQUIPMENT, hasEquipment)
        editor.putInt(FILTER_HELPER_INT, filterHelperInt)
        .apply()
    }


    fun fetchFieldType() : String?{
        return filterSettings.getString(FIELD_TYPE, "null")
    }

    fun fetchStartPrice() : Int{
        return filterSettings.getInt(START_PRICE, -1)
    }

    fun fetchEndPrice() : Int{
        return filterSettings.getInt(END_PRICE, -1)
    }

    fun fetchMinPlayers() : Int{
        return filterSettings.getInt(MIN_PLAYERS, -1)
    }

    fun fetchMaxPlayers() : Int{
        return filterSettings.getInt(MAX_PLAYERS, -1)
    }

    fun fetchDesiredDate() : String?{
        return filterSettings.getString(DESIRED_DATE, "null")
    }

    fun fetchDesiredDateStart() : String?{
        return filterSettings.getString(DESIRED_TIME_START, "null")
    }

    fun fetchDesiredDateEnd() : String?{
        return filterSettings.getString(DESIRED_TIME_END, "null")
    }

    fun fetchRatingFilter() : String?{
        return filterSettings.getString(RATING_FILTER, FILTER_BY_BOOKINGS)
    }

    fun fetchHasParking() : Boolean{
        return filterSettings.getBoolean(HAS_PARKING, false)
    }

    fun fetchIsIndoor() : Boolean{
        return filterSettings.getBoolean(IS_INDOOR, false)
    }

    fun fetchHasShowers() : Boolean{
        return filterSettings.getBoolean(HAS_SHOWERS, false)
    }

    fun fetchHasLockerRooms() : Boolean{
        return filterSettings.getBoolean(HAS_LOCKER_ROOMS, false)
    }

    fun fetchHasLights() : Boolean{
        return filterSettings.getBoolean(HAS_LIGHTS, false)
    }

    fun fetchHasRostrum() : Boolean{
        return filterSettings.getBoolean(HAS_ROSTRUM, false)
    }

    fun fetchHasEquipment() : Boolean{
        return filterSettings.getBoolean(HAS_EQUIPMENT, false)
    }

    fun fetchFilterHelperInt() : Int{
        return filterSettings.getInt(FILTER_HELPER_INT, 0)
    }


    // user Info
    fun saveContactInfo(userId: Int, name: String, number: String, type : Int){
        val editor = userInfo.edit()
        editor.putInt(USER_ID, userId)
        editor.putString(USER_NAME, name)
        editor.putString(USER_PHONE_NUMBER, number)
        editor.putInt(USER_TYPE, type)
        editor.apply()
    }
    fun deleteContactInfo(){
        val editor = userInfo.edit()
        editor.clear().apply()
    }
    fun fetchUserName() : String?{
        return userInfo.getString(USER_NAME, null)
    }
    fun fetchUserId(): Int?{
        return userInfo.getInt(USER_ID, -1)
    }
    fun fetchUserPhoneNumber(): String? {
        return userInfo.getString(USER_PHONE_NUMBER, null)
    }
    fun fetchUserType(): Int? {
        return userInfo.getInt(USER_TYPE,0)
    }
    fun saveUserType(type: Int, number: String, name: String){
        val editor = userInfo.edit()
        editor.putInt(USER_TYPE, type)
        editor.putString(USER_PHONE_NUMBER, number)
        editor.putString(USER_NAME,name)
        editor.apply()
    }
    fun saveUserId(id: Int){
        val editor = userInfo.edit()
        editor.putInt(USER_ID, id)
        editor.apply()
    }






}