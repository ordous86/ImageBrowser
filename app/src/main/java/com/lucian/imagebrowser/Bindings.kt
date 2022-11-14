package com.lucian.imagebrowser

import android.view.View.OnFocusChangeListener
import android.widget.ImageView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Interface for layout binding functions.
 */
interface Bindings

@BindingAdapter("adapter")
fun bindAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    recyclerView.adapter = adapter
}

@BindingAdapter("editorActionListener")
fun bindEditorActionListener(editor: AppCompatEditText, listener: OnEditorActionListener) {
    editor.setOnEditorActionListener(listener)
}

@BindingAdapter("isFocused")
fun bindEditorFocusBehavior(editor: AppCompatEditText, isFocused: Boolean) {
    if (isFocused) {
        editor.hint = ""
        editor.requestFocus()
    } else {
        editor.hint = editor.context.getString(R.string.keywords_hint)
        editor.clearFocus()
    }
}

@BindingAdapter("focusChangeListener")
fun bindEditorFocusListener(editor: AppCompatEditText, listener: OnFocusChangeListener) {
    editor.onFocusChangeListener = listener
}

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, url: String?) {
    val target = url ?: R.drawable.pixabay_logo
    Glide.with(imageView.context)
        .load(target)
        .placeholder(R.drawable.pixabay_logo)
        .into(imageView)
}

@BindingAdapter("layoutManager", "itemPosition")
fun bindLayoutManager(recyclerView: RecyclerView, layoutManager: RecyclerView.LayoutManager, position: Int) {
    recyclerView.layoutManager = layoutManager
    recyclerView.scrollToPosition(position)
}

@BindingAdapter("android:src")
fun bindFloatingButtonSrc(button: FloatingActionButton, flag: LayoutFlag) {
    val drawableId = when (flag) {
        LayoutFlag.LIST -> R.drawable.grid_icon
        LayoutFlag.GRID -> R.drawable.list_icon
    }
    button.setImageDrawable(ResourcesCompat.getDrawable(
        button.resources,
        drawableId,
        button.context.theme
    ))
}