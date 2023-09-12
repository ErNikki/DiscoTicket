package com.hackerini.discoticket.activities

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.hackerini.discoticket.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowImage : AppCompatActivity() {

    lateinit var bitmap:Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)

        val imageLink=intent.getSerializableExtra("image") as String
        val imageView=findViewById<ImageView>(R.id.showImageActivityImage)
        //imageView.setImageBitmap(image)

        CoroutineScope(Dispatchers.Default).launch {
            bitmap= Picasso.get().load(imageLink).get()
            runOnUiThread(Runnable {
                imageView.setImageBitmap(bitmap)
            })
        }
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        return super.onCreateView(parent, name, context, attrs)

    }
}