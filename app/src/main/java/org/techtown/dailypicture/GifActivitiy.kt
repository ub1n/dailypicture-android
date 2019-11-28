package org.techtown.dailypicture

import android.media.MediaPlayer
import android.widget.MediaController
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.photo_to_gif_5.*
import org.techtown.dailypicture.Retrofit.Response.VideoResponse
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GifActivitiy: AppCompatActivity() {
    var mediaController:MediaController?=null
    var uriPath:String?=null
    var str_url:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_to_gif_5)
        getVideoServer(1)
        //mediaController=MediaController(this)
        //mediaController!!.setAnchorView(videoView)
        //videoView.setMediaController(mediaController)

        //로딩하는동안 progressBar돌아가도록
        videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener {
            progressBar.visibility=View.GONE
        })


    }

    //서버연결 함수. uriPath 받아오기
    private fun getVideoServer(id:Int){
        //Retrofit 서버 연결
        val call= RetrofitGenerator.create().getVideo("Token "+ TokenTon.Token,id)

        call.enqueue(object : Callback<VideoResponse> {
            override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
                str_url=response.body()?.video_url.toString()
                videoView.setVideoPath(str_url)
                videoView.start()
            }
            override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
            }
        })
    }

}