package com.sadaqaworks.yorubaquran.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.sadaqaworks.yorubaquran.R

class CustomToast (context: Context ) : Toast(context) {
    private var layout: View
    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layout = inflater.inflate(R.layout.custom_toast, null)
        setGravity(Gravity.BOTTOM, 0, 50)
        duration = LENGTH_LONG
    }

    fun showToast(newText:String, background:Int? = null, toastIcon:Int? = R.drawable.play_icon){
        val text = layout.findViewById(R.id.toast_message) as TextView
        val image = layout.findViewById<ImageView>(R.id.toast_image)

        if (toastIcon == null){
            image.visibility = View.GONE
        }
        else {
            image.setImageResource(toastIcon)
        }
        text.text = newText
        if (background != null){
            layout.setBackgroundResource(background)
        }
        view= layout
        show()

    }
}

