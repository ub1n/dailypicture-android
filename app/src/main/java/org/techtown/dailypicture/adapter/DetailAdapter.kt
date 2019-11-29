package org.techtown.dailypicture.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detail_item_view.view.*
import org.techtown.dailypicture.PhotoDetailActivity
import org.techtown.dailypicture.R
import org.techtown.dailypicture.Retrofit.Response.PostResponse
import org.techtown.dailypicture.Retrofit.Response.images
import org.techtown.dailypicture.testRoom.Picture

class DetailAdapter( context: Context) :
    RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    var imageList : List<images> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.detail_item_view, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = imageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        imageList[position].let { item ->
            with(holder) {

                if (item != null) {  //main에서 사진 띄우기
                    //imageView.setImageBitmap(item.image)
/*
                    val byteArray = item.image
                    val picture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                    imageView.setImageBitmap(picture)*/
                    val thumbnail= item.url
                    Picasso.get().load(thumbnail).into(imageView)
                }
            }
        }
        //개별 이미지 선택
        holder.itemView.setOnClickListener { view ->
            //photo Detail로 넘어감
            var intent = Intent(view.context, PhotoDetailActivity::class.java)
            //이미지 정보 전달
            var byteArray = imageList[position].url
            var image_id = imageList[position].id
            intent.putExtra("image", byteArray)
            intent.putExtra("image_id", image_id)
            view.context.startActivity(intent)
        }
    }
    fun setGoalListItems(imageList: List<images>){
        this.imageList = imageList;
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.imageView

    }
}