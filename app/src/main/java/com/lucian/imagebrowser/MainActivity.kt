package com.lucian.imagebrowser

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.paging.toLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lucian.imagebrowser.databinding.ActivityMainBinding

/**
 * The entry of this application.
 */
class MainActivity: AppCompatActivity() {
    // Fields.
    private val displaySize: Point by lazy {
        Point().also {
            this.windowManager.defaultDisplay.getSize(it)
        }
    }
    private val editorActionListener = OnEditorActionListener { view, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            onSearchKeywords(view.text.toString())
        }
        false
    }
    private val focusChangeListener = OnFocusChangeListener { _, hasFocus ->
        viewModel.isFocusEditor.value = hasFocus
    }
    private val imageAdapter: ImageAdapter by lazy {
        ImageAdapter(this@MainActivity, viewModel, displaySize)
    }
    private val searchHistoryAdapter: SearchHistoryAdapter by lazy {
        SearchHistoryAdapter(this@MainActivity, searchHistoryListener)
    }
    private val searchHistoryListener = object: SearchHistoryListener {
        override fun onDelete(history: String) {
            // delete search history from shared preferences
            val dataset = HashSet<String>(sharedPref.getStringSet(SHARED_PREF_KEY, emptySet()) ?: emptySet())
            dataset.remove(history)
            sharedPref.edit().putStringSet(SHARED_PREF_KEY, dataset).apply()

            // submit the remaining data
            searchHistoryAdapter.submitList(dataset.toList())
        }
        override fun onLoad(history: String) = onSearchKeywords(history)
    }
    private val sharedPref: SharedPreferences by lazy {
        applicationContext.getSharedPreferences(SHARED_PREF_FILE, 0)
    }
    private lateinit var viewBinding: ActivityMainBinding
    private val viewModel: ImageViewModel by lazy {
        ViewModelProvider(this, ImageViewModelFactory())[ImageViewModel::class.java]
    }

    // Define interface for search history.
    interface SearchHistoryListener {
        fun onDelete(history: String)
        fun onLoad(history: String)
    }

    // Called when screen configuration changed.
    override fun onConfigurationChanged(newConfig: Configuration) {
        // call super
        super.onConfigurationChanged(newConfig)

        // update orientation
        viewModel.orientationFlag.value = newConfig.orientation
    }

    // Create.
    override fun onCreate(savedInstanceState: Bundle?) {
        // call super
        super.onCreate(savedInstanceState)

        // initialize binding
        viewBinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).also { binding ->
            binding.searchHistoryLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.imageLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.imageGridLayoutManager = GridLayoutManager(this, GRID_SPAN_COUNT)
            binding.imageAdapter = imageAdapter
            binding.searchHistoryAdapter = searchHistoryAdapter
            binding.editorActionListener = editorActionListener
            binding.focusChangeListener = focusChangeListener
            binding.viewModel = viewModel
            binding.lifecycleOwner = this
        }

        // prepare search history
        val dataset = sharedPref.getStringSet(SHARED_PREF_KEY, emptySet()) ?: emptySet()
        searchHistoryAdapter.submitList(dataset.toList())

        // observe editor focus state
        viewModel.isFocusEditor.observe(this) { isFocus ->
            if (!isFocus) {
                val imeManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imeManager.hideSoftInputFromWindow(viewBinding.keywordsEditor.windowToken, 0)
            }
        }

        // observe online query state
        viewModel.queryState.observe(this) { state ->
            val toastId = when (state) {
                QueryState.NO_RESULT -> R.string.toast_no_result
                QueryState.NETWORK_ERROR -> R.string.toast_network_error
                else -> return@observe
            }
            Toast.makeText(this, toastId, Toast.LENGTH_LONG).show()
        }
    }

    // Called when enter keywords for search.
    private fun onSearchKeywords(keywords: String) {
        // check keywords
        val refinedKeywords = keywords.trim().replace("\\s+".toRegex(), " ")
        if (TextUtils.isEmpty(refinedKeywords)) {
            Toast.makeText(this, R.string.toast_no_keywords, Toast.LENGTH_LONG).show()
            return
        }

        // update data binding and live data
        viewModel.keywords.set(refinedKeywords)
        viewModel.itemPosition.set(0)
        viewModel.queryState.value = QueryState.RUNNING

        // update and save search history
        val dataset = HashSet<String>(sharedPref.getStringSet(SHARED_PREF_KEY, emptySet()) ?: emptySet())
        if (!dataset.contains(refinedKeywords)) {
            dataset.add(refinedKeywords)
            sharedPref.edit().putStringSet(SHARED_PREF_KEY, dataset).apply()
            searchHistoryAdapter.submitList(dataset.toList())
        }

        // defocus editor
        viewBinding.keywordsEditor.clearFocus()

        // clear previous result
        if (imageAdapter.itemCount > 0) {
            imageAdapter.notifyItemRangeRemoved(0, imageAdapter.itemCount)
        }

        // observe new result
        ImageDataSourceFactory(viewModel).toLiveData(PAGE_SIZE).observe(this) { result ->
            imageAdapter.submitList(result)
        }
    }

    // Called when swap layout between list and grid.
    fun onSwapLayout(view: View) {
        when (viewModel.layoutFlag.value) {
            // from list to grid
            LayoutFlag.LIST -> {
                viewModel.layoutFlag.value = LayoutFlag.GRID
                viewModel.itemPosition.set(viewBinding.imageLinearLayoutManager?.findFirstVisibleItemPosition())
            }

            // from grid to list
            LayoutFlag.GRID -> {
                viewModel.layoutFlag.value = LayoutFlag.LIST
                viewModel.itemPosition.set(viewBinding.imageGridLayoutManager?.findFirstVisibleItemPosition())
            }

            // unexpected case
            else -> throw IllegalArgumentException()
        }
        viewBinding.imageRecyclerView.invalidate()
    }
}