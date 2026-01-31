package com.saiesh.tele.presentation.media.presenter

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.saiesh.tele.domain.model.media.VideoChatItem

class VideoChatPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val context = parent.context
        val textView = TextView(context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setPadding(32, 24, 32, 24)
            gravity = Gravity.CENTER_VERTICAL
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        val chat = item as? VideoChatItem ?: return
        val textView = viewHolder.view as TextView
        textView.text = chat.title
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) = Unit
}
