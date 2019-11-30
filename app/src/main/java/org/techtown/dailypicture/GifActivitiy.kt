package org.techtown.dailypicture

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.photo_to_gif_5.*
import org.techtown.dailypicture.Retrofit.Response.VideoResponse
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class GifActivitiy: AppCompatActivity() {
    var mediaController:MediaController?=null
    var uriPath:Uri?=null
    var str_url:String?=null
    var uri:Uri?=null
    private var downloadId: Long = -1L
    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 0

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent
        ) { //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                //Toast.makeText(this@GifActivity, "Download Completed", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_to_gif_5)
        getVideoServer(TokenTon.postId!!)
        //Log.d("str",str_url)
        //mediaController = MediaController(this)
        //mediaController!!.setAnchorView(videoView)
        //videoView.setMediaController(mediaController)
        //videoView.setVideoPath("https://elasticbeanstalk-ap-northeast-2-085345381111.s3.amazonaws.com/media/video/57/keyboard.mp4")
        //videoView.start()
        //로딩하는동안 progressBar돌아가도록
        videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener {
            progressBar.visibility = View.GONE

        })
        //
        //val uriPath="https://www.demonuts.com/Demonuts/smallvideo.mp4"
        //videoView.setVideoPath("https://www.ebookfrenzy.com/android_book/movie.mp4")
        //videoView.start()
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager


        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        download_gif.setOnClickListener {
            beginDownload()
        }
    }


    private fun getVideoServer(id:Int){
        //Retrofit 서버 연결
        val call= RetrofitGenerator.create().getVideo("Token "+ TokenTon.Token,id)

        call.enqueue(object : Callback<VideoResponse> {
            override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
                str_url=response.body()?.video_url.toString()
                TokenTon.setvideoPath(str_url!!)
                videoView.setVideoURI(Uri.parse(str_url))
                videoView.start()
            }
            override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
            }
        })
    }

    //다운로드매니저 방법1
    private fun downloadImage() {
        val file = File(getExternalFilesDir(null), "dev_submit.mp4")
        val youtubeUrl = "https://elasticbeanstalk-ap-northeast-2-085345381111.s3.amazonaws.com/media/video/59/3333.mp4"

        val request = DownloadManager.Request(Uri.parse(youtubeUrl))
            .setTitle("Downloading a video")
            .setDescription("Downloading Dev Summit")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(Uri.fromFile(file))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        downloadId = downloadManager.enqueue(request)
        Log.d("path", "path : " + file.path)
    }

    //다운로드 매니저 방법2
    private fun beginDownload() {
        val file = File(getExternalFilesDir(null), "daily.mp4")
        /*
        Create a DownloadManager.Request with all the information necessary to start the download
         */
        val request =
            DownloadManager.Request(Uri.parse("https://elasticbeanstalk-ap-northeast-2-085345381111.s3.amazonaws.com/media/video/59/3333.mp4"))
                .setTitle("Dummy File") // Title of the Download Notification
                .setDescription("Downloading") // Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE) // Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file)) // Uri of the destination file
                .setRequiresCharging(false) // Set if charging is required to begin the download
                .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true) // Set if download is allowed on roaming network
        val downloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.
    }
}