package com.lucian.imagebrowser

import androidx.paging.DataSource
import com.lucian.imagebrowser.ImageRepository.ImageData

/**
 * Factory to create [ImageDataSource].
 */
class ImageDataSourceFactory(private val viewModel: ImageViewModel): DataSource.Factory<Int, ImageData>() {
    // Create data source.
    override fun create() = ImageDataSource(viewModel)
}