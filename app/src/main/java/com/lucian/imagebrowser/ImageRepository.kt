package com.lucian.imagebrowser

import com.google.gson.annotations.SerializedName
import com.lucian.imagebrowser.WebService.WebDataStruct

/**
 * Repository of image information.
 */
class ImageRepository {
    // Define data structure for image item.
    data class ImageData (
        @SerializedName("webformatURL")
        val imageUrl: String,

        @SerializedName("webformatWidth")
        val imageWidth: String,

        @SerializedName("webformatHeight")
        val imageHeight: String
    )

    // Request online data with specified params.
    suspend fun requestOnlineData(keywords: String, page: Int): WebDataStruct =
        WebService.api.requestOnlineData(keywords, page)
}