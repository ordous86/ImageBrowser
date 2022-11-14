package com.lucian.imagebrowser

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Point
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lucian.imagebrowser.ImageAdapter.ImageViewHolder
import com.lucian.imagebrowser.ImageRepository.ImageData
import com.lucian.imagebrowser.databinding.ImageItemBinding

/**
 * Adapter for image.
 */
class ImageAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: ImageViewModel,
    private val displaySize: Point): PagedListAdapter<ImageData, ImageViewHolder>(DiffCallback) {

    // Callback for calculating the diff between 2 list items.
    companion object DiffCallback: DiffUtil.ItemCallback<ImageData>() {
        override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData) = (oldItem === newItem)
        override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData) =
            (oldItem.imageUrl == newItem.imageUrl)
    }

    // Define view holder for adapter.
    inner class ImageViewHolder(private val binding: ImageItemBinding): RecyclerView.ViewHolder(binding.root) {
        // Set data to list item.
        fun setData(data: ImageData) {
            binding.imageData = data
            binding.executePendingBindings()
        }

        // Update item size.
        fun updateSize(layoutFlag: LayoutFlag?, orientation: Int?) {
            // check image size
            val imageWidth = binding.imageData?.imageWidth?.toInt() ?: return
            val imageHeight = binding.imageData?.imageHeight?.toInt() ?: return

            // check screen size
            val screenWidth = when (orientation) {
                ORIENTATION_LANDSCAPE -> displaySize.y
                ORIENTATION_PORTRAIT -> displaySize.x
                else -> return
            }

            // check layout
            val reference = when (layoutFlag) {
                LayoutFlag.GRID -> screenWidth / GRID_SPAN_COUNT
                LayoutFlag.LIST -> screenWidth
                else -> return
            }

            // request change
            binding.itemImageView.layoutParams.width = reference
            binding.itemImageView.layoutParams.height = reference * imageHeight / imageWidth
            binding.itemImageView.requestLayout()
        }
    }

    // Bind.
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        getItem(position)?.also {
            holder.setData(it)
            holder.updateSize(viewModel.layoutFlag.value, viewModel.orientationFlag.value)
        }
    }

    // Create.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        // initialize binding
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // initialize view holder
        val holder = ImageViewHolder(binding)

        // prepare binding variables
        binding.lifecycleOwner = lifecycleOwner

        // observe layout change
        viewModel.layoutFlag.observe(lifecycleOwner) { flag ->
            holder.updateSize(flag, viewModel.orientationFlag.value)
        }

        // observe orientation change
        viewModel.orientationFlag.observe(lifecycleOwner) { orientation ->
            holder.updateSize(viewModel.layoutFlag.value, orientation)
        }

        // complete
        return holder
    }
}