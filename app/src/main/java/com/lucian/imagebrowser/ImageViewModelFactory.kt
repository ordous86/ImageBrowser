package com.lucian.imagebrowser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

/**
 * Factory to build [ImageViewModel].
 */
@Suppress("UNCHECKED_CAST")
class ImageViewModelFactory: ViewModelProvider.Factory {
    // Create view model.
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        // create
        modelClass.isAssignableFrom(ImageViewModel::class.java) -> ImageViewModel() as T

        // otherwise
        else -> throw IllegalArgumentException("Unknown ViewModel")
    }
}