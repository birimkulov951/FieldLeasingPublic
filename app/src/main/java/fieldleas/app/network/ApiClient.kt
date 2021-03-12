package fieldleas.app.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

    private lateinit var apiService: ApiService


    companion object {
        private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
    }
    private val client = OkHttpClient.Builder().apply {
        this.addInterceptor(RetrofitInstance.interceptor)
    }.build()

    fun getApiService(context: Context): ApiService {
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                    .baseUrl(fieldleas.app.utils.Constants.BASE_URL)
                    //.client(client)
                    .client(okhttpClient(context)) // Add our Okhttp client
                    .build()

            apiService = retrofit.create(ApiService::class.java)
        }

        return apiService
    }

    /**
     * Initialize OkhttpClient with our interceptor
     */
    private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .build()
    }


    fun getApiService(): ApiService {

        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                    .baseUrl(fieldleas.app.utils.Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            apiService = retrofit.create(ApiService::class.java)
        }

        return apiService
    }

}