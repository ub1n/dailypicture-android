package org.techtown.dailypicture

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.app.ShareCompat.IntentBuilder
import androidx.core.content.FileProvider
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.photo_to_gif_5.*
import org.techtown.dailypicture.Retrofit.Response.VideoResponse
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import org.techtown.dailypicture.GifActivitiy as Gif


class GifActivitiy: AppCompatActivity() {
    var mediaController:MediaController?=null
    var uriPath:Uri?=null
    var str_url:String?=null
    var uri:Uri?=null
    private var downloadId: Long = -1L
    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 0
    var goal_name:String?=null
    var storageDir:File?=null

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
        goal_name=getIntent().getStringExtra("goal_name")
        goalText.setText(goal_name)

        //Log.d("str",str_url)
        mediaController = MediaController(this)
        mediaController!!.setMediaPlayer(videoView)
        mediaController!!.setAnchorView(videoView)
        videoView.setMediaController(mediaController)


        //로딩하는동안 progressBar돌아가도록
        videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener {
            progressBar.visibility = View.GONE

        })

        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        download_gif.setOnClickListener {
            Toast.makeText(applicationContext,"다운로드를 시작합니다.",Toast.LENGTH_LONG).show()
            beginDownload()
        }
        back_gif.setOnClickListener {
            finish()
        }
        /*share_gif.setOnClickListener {
            var uri=Uri.parse(str_url)
            //var uriToImage=FileProvider.getUriForFile(applicationContext,FILES_AUTHORITY,)
            var shareIntent= IntentBuilder
                .from(this)
                .setStream(uri)
                .intent
            shareIntent.setData(uri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            //if(shareIntent.resolveActivity(packageManager)!=null){
                startActivity(shareIntent)
            //}

        }*/
    }


    private fun getVideoServer(id:Int){
        //Retrofit 서버 연결
        val call= RetrofitGenerator.create().getVideo("Token "+ TokenTon.Token,id)

        call.enqueue(object : Callback<VideoResponse> {
            override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
                if(response.isSuccessful==false){
                    ServerError()
                }else{
                str_url=response.body()?.video_url.toString()
                TokenTon.setvideoPath(str_url!!)
                videoView.setVideoURI(Uri.parse(str_url))
                videoView.start()}
            }
            override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
                ServerError()
            }
        })
    }

    //다운로드 매니저
    private fun beginDownload() {
        //val file = File(getExternalFilesDir(null), goal_name+".mp4")
        storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/DailyPicture",goal_name+".mp4")
        val request =
            DownloadManager.Request(Uri.parse(str_url))
                .setTitle("Dummy File") // Title of the Download Notification
                .setDescription("Downloading") // Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE) // Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(storageDir)) // Uri of the destination file
                //.setDestinationInExternalFilesDir(applicationContext,storageDir,goal_name)
                .setRequiresCharging(false) // Set if charging is required to begin the download
                .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true) // Set if download is allowed on roaming network
        val downloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.
    }


    private fun ServerError(){
        Toast.makeText(this,"서버와의 연결이 종료되었습니다.초기화면으로 돌아갑니다",Toast.LENGTH_LONG).show()

        val intent=Intent(this,LoadingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityForResult(intent,2)
        finish()
    }

}