package com.lucian.imagebrowser

/**
 * Define constants for global access.
 */
interface Constants

const val GRID_SPAN_COUNT = 2
const val PAGE_SIZE = 20
const val SHARED_PREF_FILE = "user_search_history"
const val SHARED_PREF_KEY = "user_keywords"
const val SOURCE_URL = "https://pixabay.com/api/"
const val TAG = "ImageBrowserApp"

enum class LayoutFlag {
    GRID,
    LIST
}

enum class QueryState {
    IDLE,
    NETWORK_ERROR,
    NO_RESULT,
    RUNNING,
    SUCCESS
}