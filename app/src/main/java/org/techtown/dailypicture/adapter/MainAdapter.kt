package org.techtown.dailypicture.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detail_item_view.view.*
import kotlinx.android.synthetic.main.goal_item_view.view.*
import org.techtown.dailypicture.GoalDetailActivity
import org.techtown.dailypicture.PhotoDetailActivity

import org.techtown.dailypicture.R
import org.techtown.dailypicture.Retrofit.Response.PostResponse
import org.techtown.dailypicture.testRoom.Goal
import org.techtown.dailypicture.testRoom.Picture
import org.techtown.dailypicture.utils.TokenTon
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.util.*
import java.net.HttpURLConnection

class MainAdapter(context : Context) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    var goalList : List<PostResponse> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_item_view, parent, false)
        //이미지 위에 어둡게 씌우는 것
        view.goal_imageView.setColorFilter(Color.parseColor("#882C2C2C"))
        return ViewHolder(view)
    }

    override fun getItemCount() = goalList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        goalList[position].let{ item ->
            with(holder) {
                if(item.thumbnail!=null) {  //main에서 사진 띄우기
                    //imageView.setImageBitmap(item.image)

                    val thumbnail= item.thumbnail
                    /*val url: URL =URL(thumbnail)
                    val conn:HttpURLConnection=url.openConnection() as HttpURLConnection
                    conn.setDoInput(true)
                    conn.connect()
                    val iss:InputStream=conn.inputStream
                    //val iss: InputStream =url.openConnection().inputStream

                    val picture= BitmapFactory.decodeStream(iss)*/
                    //goal_imageView.setImageBitmap(picture)
                    Picasso.get().load(thumbnail).into(goal_imageView)
                    //item 위에 글자쓰기
                    goal_text.setText(item.title)
                    goal_text2.setText("D+"+item.dday.toString())


                }
            }
        }

        //각 item에 클릭 이벤트 붙이기
        holder.itemView.setOnClickListener { view->
            var intent=Intent(view.context, GoalDetailActivity::class.java)
            intent.putExtra("goal_name",goalList[position].title)
            intent.putExtra("goal_id",goalList[position].id)
            intent.putExtra("goal_dday",goalList[position].dday)
            TokenTon.setpostId(goalList[position].id)
            view.context.startActivity(intent)
        }



    }
    fun setGoalListItems(goalList: List<PostResponse>){
        this.goalList = goalList;
        notifyDataSetChanged()
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val goal_imageView: ImageView =view.goal_imageView
        val goal_text: TextView =view.goal_imageView_text
        val goal_text2:TextView=view.goal_imageView_text2
    }
}