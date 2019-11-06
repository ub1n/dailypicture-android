package org.techtown.dailypicture.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.detail_item_view.view.*
import kotlinx.android.synthetic.main.goal_item_view.view.*
import org.techtown.dailypicture.GoalDetailActivity
import org.techtown.dailypicture.PhotoDetailActivity

import org.techtown.dailypicture.R
import org.techtown.dailypicture.testRoom.Goal
import org.techtown.dailypicture.testRoom.Picture
import java.io.ByteArrayOutputStream
import java.util.*

class MainAdapter(private var goalList : List<Goal>, context : Context) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item_view, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = goalList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        goalList[position].let{ item ->
            with(holder) {
                if(item.image!=null) {  //main에서 사진 띄우기
                    //imageView.setImageBitmap(item.image)
                    val byteArray= item.image
                    val picture= BitmapFactory.decodeByteArray(byteArray,0,byteArray!!.size)
                    goal_imageView.setImageBitmap(picture)

                }
            }
        }

        //각 item에 클릭 이벤트 붙이기
        holder.itemView.setOnClickListener { view->
            var intent=Intent(view.context, GoalDetailActivity::class.java)
            intent.putExtra("goal_name",goalList[position].goal_name)
            intent.putExtra("goal_id",goalList[position].id)
            view.context.startActivity(intent)
        }



    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val goal_imageView: ImageView =view.goal_imageView
    }
}