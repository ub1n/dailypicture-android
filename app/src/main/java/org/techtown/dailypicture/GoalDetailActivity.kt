package org.techtown.dailypicture

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.add_goal_2.*
import kotlinx.android.synthetic.main.goal_detail_3.*
import org.techtown.dailypicture.adapter.DetailAdapter
import org.techtown.dailypicture.testRoom.Picture
import org.techtown.dailypicture.testRoom.PictureDao
import org.techtown.dailypicture.testRoom.PictureDatabase
import java.io.ByteArrayOutputStream
import java.util.*

class GoalDetailActivity: AppCompatActivity() { //여긴 싹다 임시(recyclerview 테스트용)
    var picture= Picture()
    private var pictureDatabase:PictureDatabase?=null
    private var pictureList=listOf<Picture>()
    lateinit var mAdapter:DetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goal_detail_3)
        pictureDatabase=PictureDatabase.getInstance(this)

        mAdapter= DetailAdapter(pictureList,applicationContext)
        val r = Runnable {   //recyclerview와 android room 결합
            try {
                pictureList = pictureDatabase?.pictureDao?.getPicture()!!
                mAdapter = DetailAdapter(pictureList, applicationContext)
                mAdapter.notifyDataSetChanged()

                detailRecyclerView.adapter = mAdapter
                detailRecyclerView.layoutManager = LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL,false)

                detailRecyclerView.setHasFixedSize(true)
            } catch (e: Exception) {
                Log.d("tag", "Error - $e")
            }
        }

        val thread = Thread(r)
        thread.start()




        cameraButton.setOnClickListener { //임시
            var intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,123)
        }
        gifbutton.setOnClickListener {
            var intent=Intent(this,GifActivitiy::class.java)
            startActivityForResult(intent,3)
        }
        //뒤로가기 버튼
        back_goal_detail.setOnClickListener{
            var intent=Intent(this,MainActivity::class.java)
            startActivityForResult(intent,3)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123){

            var bmp=data?.extras?.get("data") as Bitmap
            var stream= ByteArrayOutputStream()
            bmp?.compress(Bitmap.CompressFormat.JPEG,100,stream)
            var byteArray=stream.toByteArray()

            picture.image=byteArray
            val database: PictureDatabase = PictureDatabase.getInstance(applicationContext)
            val pictureDao: PictureDao =database.pictureDao

            Thread { database.pictureDao.insert(picture) }.start()
            var intent=Intent(this,GoalDetailActivity::class.java)
            startActivity(intent)
            this.finish()

        }
    }
}