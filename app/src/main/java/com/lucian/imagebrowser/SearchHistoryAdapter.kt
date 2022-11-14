package com.lucian.imagebrowser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lucian.imagebrowser.SearchHistoryAdapter.HistoryViewHolder
import com.lucian.imagebrowser.databinding.SearchHistroyItemBinding
import com.lucian.imagebrowser.MainActivity.SearchHistoryListener

/**
 * Adapter for search history.
 */
class SearchHistoryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val listener: SearchHistoryListener): ListAdapter<String, HistoryViewHolder>(DiffCallback) {

    // Callback for calculating the diff between 2 list items.
    companion object DiffCallback: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = (oldItem === newItem)
        override fun areContentsTheSame(oldItem: String, newItem: String) = (oldItem == newItem)
    }

    // Define view holder for adapter.
    inner class HistoryViewHolder(private val binding: SearchHistroyItemBinding): RecyclerView.ViewHolder(binding.root) {
        // Set search history.
        fun setHistory(history: String) {
            binding.history = history
            binding.executePendingBindings()
        }
    }

    // Create.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        // initialize binding
        val binding = SearchHistroyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // initialize view holder
        val holder = HistoryViewHolder(binding)

        // prepare binding variables
        binding.searchHistoryListener = listener
        binding.lifecycleOwner = lifecycleOwner

        // complete
        return holder
    }

    // Bind.
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        getItem(position)?.also {
            holder.setHistory(it)
        }
    }
}