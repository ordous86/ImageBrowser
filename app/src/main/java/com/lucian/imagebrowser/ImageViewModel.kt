package com.lucian.imagebrowser

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel to connect [MainActivity] and [ImageRepository].
 */
class ImageViewModel: ViewModel() {
    // Fields.
    private val repository = ImageRepository()

    // Fields for data binding.
    val itemPosition = ObservableField<Int>().apply { set(0) }
    val keywords = ObservableField<String>().apply { set("") }

    // Fields for live data.
    val isFocusEditor = MutableLiveData<Boolean>().apply { value = false }
    val layoutFlag = MutableLiveData<LayoutFlag>().apply { value = LayoutFlag.LIST }
    val orientationFlag = MutableLiveData<Int>().apply { value = ORIENTATION_PORTRAIT }
    val queryState = MutableLiveData<QueryState>().apply { value = QueryState.IDLE }

    // Cancel focus on search history editor.
    fun cancelFocusEditor() {
        isFocusEditor.value = false
    }

    // Query online with specified params.
    suspend fun queryOnline(keywords: String, page: Int) = repository.requestOnlineData(keywords, page)
}