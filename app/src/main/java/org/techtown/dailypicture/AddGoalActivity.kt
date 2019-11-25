package org.techtown.dailypicture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.add_goal_2.*
import org.techtown.dailypicture.testRoom.Goal
import org.techtown.dailypicture.testRoom.GoalDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import org.techtown.dailypicture.Retrofit.Request.PostRequest
import org.techtown.dailypicture.Retrofit.Response.PostResponse
import org.techtown.kotlin_todolist.RetrofitGenerator
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddGoalActivity: AppCompatActivity() {
    var GET_GALLERY_IMAGE:Int=200
    var goal= Goal()
    private var goalDatabase:GoalDatabase?=null
    var title:String?=null
    var thumbnail:String?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_goal_2)

        //뒤로가기 버튼
        goal_back.setOnClickListener {
            var intent= Intent(this,MainActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            this.finish()
        }

        //목표 등록 확인 버튼
        button_add.setOnClickListener {
            if(goal_input_add.length()==0){
                Toast.makeText(applicationContext,"목표 이름을 설정해주세요", Toast.LENGTH_LONG).show()
            }else {
                goal.goal_name=goal_input_add.text.toString()
                //val database:GoalDatabase=GoalDatabase.getInstance(applicationContext)
                //val goalDao: GoalDao =database.goalDao
                //Thread{database.goalDao.insert(goal)}.start()
                //var intent = Intent(this, MainActivity::class.java)
                //startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                //this.finish()
                title=goal_input_add.text.toString();
                //PostServer(goal_input_add.text.toString(),thumbnail,true);
            }
        }

        //대표 사진 설정 부분
        imageView_add.setOnClickListener {
            var intent=Intent(Intent.ACTION_PICK);
            intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")
            startActivityForResult(intent,GET_GALLERY_IMAGE)
        }
    }
/*
    private fun PostServer(title:String,thumbnail:String,status:Boolean){
        val postRequest= PostRequest(title,thumbnail,status)
        val call= RetrofitGenerator.create().registerPost(postRequest)
        val intent=Intent(this,MainActivity::class.java)
        call.enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                //Log.d("success", response.body()?.username.toString())
                Log.d("success", response.code().toString())
                when(response!!.code()){
                    201->{
                        Toast.makeText(this@AddGoalActivity,"목표 등록 성공",Toast.LENGTH_LONG).show()
                        startActivity(intent)
                        finish()
                    }
                    405->Toast.makeText(this@AddGoalActivity,"목표 등록 실패",Toast.LENGTH_LONG).show()
                    500->Toast.makeText(this@AddGoalActivity,"서버 오류",Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                Log.d("fail", "failed")
            }
        })

    }

 */

    //갤러리에서 이미지 불러오는 것
    //밑 SuppressLint는 에러나서 추가함
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            imageView_add.setImageURI(selectedImageUri)

            if (selectedImageUri != null) {
                goal.image=convertImageToByte(selectedImageUri)
                thumbnail=convertImageToByte(selectedImageUri).toString();
            }

        }
    }

    //Uri를 ByteArray로 변환하는 함수
    fun convertImageToByte(uri: Uri): ByteArray? {
        var data: ByteArray? = null
        try {
            val cr = baseContext.contentResolver
            val inputStream = cr.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            data = baos.toByteArray()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return data
    }
}
