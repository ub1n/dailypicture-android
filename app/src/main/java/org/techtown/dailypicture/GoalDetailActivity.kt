package org.techtown.dailypicture

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_goal_2.*
import kotlinx.android.synthetic.main.goal_detail_3.*
import org.techtown.dailypicture.adapter.DetailAdapter
import org.techtown.dailypicture.adapter.MainAdapter
import org.techtown.dailypicture.testRoom.*
import java.io.ByteArrayOutputStream
import java.util.*

class GoalDetailActivity: AppCompatActivity() { //여긴 싹다 임시(recyclerview 테스트용)
    var picture= Picture()
    private var pictureDatabase:PictureDatabase?=null
    private var pictureList=listOf<Picture>()
    lateinit var mAdapter:DetailAdapter
    var goal= Goal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goal_detail_3)

        //목표 이름 불러오기
        var goal_name=getIntent().getStringExtra("goal_name")
        var goal_id=getIntent().getIntExtra("goal_id",0)
        goalText.setText(goal_name)
        pictureDatabase=PictureDatabase.getInstance(this)

        //이미지 보여주는 recyclerview
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



        //카메라 아이콘 버튼
        cameraButton.setOnClickListener { //임시
            //var intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            var intent=Intent(this,CameraActivity::class.java)
            startActivityForResult(intent,123)
        }
        //내보내기 버튼
        gifbutton.setOnClickListener {
            var intent=Intent(this,GifActivitiy::class.java)
            startActivityForResult(intent,3)
        }
        //뒤로가기 버튼
        back_goal_detail.setOnClickListener{
            var intent=Intent(this,MainActivity::class.java)
            startActivityForResult(intent,3)
        }
        //삭제하기 버튼
        delete_goal_detail.setOnClickListener {
            //goal 테이블에서 목표 delete
            val database: GoalDatabase = GoalDatabase.getInstance(applicationContext)
            val goalDao: GoalDao =database.goalDao
            Thread{database.goalDao.delete(goal_id)}.start()
            var intent=Intent(this,MainActivity::class.java)
            startActivityForResult(intent,3)
        }

    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123){
            if(data!=null){
                Toast.makeText(this,"save",Toast.LENGTH_LONG).show()
                val img=data.getByteArrayExtra("data")
           *//* var bmp=data?.extras?.get("data") as Bitmap
            var stream= ByteArrayOutputStream()
            bmp?.compress(Bitmap.CompressFormat.JPEG,100,stream)
            var byteArray=stream.toByteArray()*//*

            picture.image=img
            val database: PictureDatabase = PictureDatabase.getInstance(applicationContext)
            val pictureDao: PictureDao =database.pictureDao

            Thread { database.pictureDao.insert(picture) }.start()
            var intent=Intent(this,GoalDetailActivity::class.java)
            startActivity(intent)
            this.finish()}

        }else{
            Toast.makeText(this,"non-save",Toast.LENGTH_LONG).show()
        }
    }*/
}