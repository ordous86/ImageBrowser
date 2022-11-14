package com.lucian.imagebrowser

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.lucian.imagebrowser.ImageRepository.ImageData

/**
 * Service for web access.
 */
object WebService {
    // Load native lib.
    init {
        System.loadLibrary("ApiKeyGenerator")
    }

    // Get api key from native.
    @JvmStatic
    private external fun nativeGetApiKey(): String

    // Fields.
     val api: WebApi by lazy {
        Retrofit.Builder()
            .baseUrl(SOURCE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebApi::class.java)
    }

    // Define interface for web APIs.
    interface WebApi {
        @GET("?per_page=$PAGE_SIZE&image_type=photo")
        suspend fun requestOnlineData(
            @Query("q") keywords: String,
            @Query("page") page: Int,
            @Query("key") key: String = nativeGetApiKey()): WebDataStruct
    }

    // Define structure for web data.
    data class WebDataStruct (
        @SerializedName("totalHits")
        var size: Int,

        @SerializedName("hits")
        var resultList: List<ImageData>
    )
}