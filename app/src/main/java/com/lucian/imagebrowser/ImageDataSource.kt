package com.lucian.imagebrowser

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PageKeyedDataSource
import com.lucian.imagebrowser.ImageRepository.ImageData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Data source for image.
 */
class ImageDataSource(private val viewModel: ImageViewModel): PageKeyedDataSource<Int, ImageData>() {
    // Fields.
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "CoroutineExceptionHandler() - An exception occurs: ", throwable.fillInStackTrace())
        viewModel.queryState.value = QueryState.NETWORK_ERROR
    }

    // Called to load initial data.
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ImageData>) {
        // encode keywords
        val encodedKeywords = viewModel.keywords.get()?.replace("\\s+".toRegex(), "+") ?: return
        Log.d(TAG, "loadInitial() - Encoded keywords = $encodedKeywords")

        // query online
        viewModel.viewModelScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
            val webData = viewModel.queryOnline(encodedKeywords, 1)
            webData.resultList.also { list ->
                if (list.isEmpty()) {
                    // update query state
                    viewModel.queryState.value = QueryState.NO_RESULT

                    // callback result
                    callback.onResult(list, null, null)
                    Log.w(TAG, "loadInitial() - Empty body")
                } else {
                    // determine default layout by total count
                    viewModel.layoutFlag.value = if (webData.size > 100)
                        LayoutFlag.GRID
                    else
                        LayoutFlag.LIST

                    // update query state
                    viewModel.queryState.value = QueryState.SUCCESS

                    // callback result
                    callback.onResult(list, null, 2)
                    Log.d(TAG, "loadInitial() - Success")
                }
            }
        }
    }

    // Called to load data on previous page.
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ImageData>) {}

    // Called to load data on next page.
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ImageData>) {
        // encode keywords
        val encodedKeywords = viewModel.keywords.get()?.replace("\\s+".toRegex(), "+") ?: return
        Log.d(TAG, "loadAfter() - Encoded keywords = $encodedKeywords")

        // query online
        viewModel.viewModelScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
            val webData = viewModel.queryOnline(encodedKeywords, params.key)
            webData.resultList.also { list ->
                if (list.isEmpty()) {
                    callback.onResult(list, null)
                    Log.w(TAG, "loadAfter() - Empty body")
                } else {
                    callback.onResult(list, params.key + 1)
                    Log.d(TAG, "loadAfter() - Success")
                }
            }
        }
    }
}