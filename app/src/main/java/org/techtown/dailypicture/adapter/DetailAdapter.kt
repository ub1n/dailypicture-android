package org.techtown.dailypicture.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.detail_item_view.view.*
import org.techtown.dailypicture.PhotoDetailActivity

import org.techtown.dailypicture.R
import org.techtown.dailypicture.testRoom.Picture
import java.io.ByteArrayOutputStream
import java.util.*

class DetailAdapter(private var pictureList : List<Picture>, context : Context) : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_item_view, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = pictureList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        pictureList[position].let{ item ->
            with(holder) {

                if(item.image!=null) {  //main에서 사진 띄우기
                    //imageView.setImageBitmap(item.image)
                    val byteArray= item.image
                    val picture= BitmapFactory.decodeByteArray(byteArray,0,byteArray!!.size)
                    imageView.setImageBitmap(picture)
                }
            }
        }
        holder.itemView.setOnClickListener { view->   //수정에 정보날림
            var intent=Intent(view.context,PhotoDetailActivity::class.java)
            view.context.startActivity(intent)
        }



    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView =view.imageView

    }
}