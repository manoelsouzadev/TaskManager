package br.edu.ifsp.scl.moviesmanager.util

import android.graphics.Color
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.DateFormat

@BindingAdapter("setPriority")
fun setPriority(view: TextView, priority: Int) {
    when(priority) {
        0 -> {
            view.setText("High Priority")
            view.setTextColor(Color.RED)
        }
        1 -> {
            view.setText("Medium Priority")
            view.setTextColor(Color.BLUE)
        }
        else -> {
            view.setText("Low Priority")
            view.setTextColor(Color.GREEN)
        }
    }

    @BindingAdapter("setTimestamp")
    fun setTimestamp(view: TextView, timestamp: Long) {
        view.text = DateFormat.getInstance().format(timestamp)
    }
}