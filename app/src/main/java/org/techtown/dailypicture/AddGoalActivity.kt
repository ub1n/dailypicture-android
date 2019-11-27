package org.techtown.dailypicture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.add_goal_2.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.techtown.dailypicture.Retrofit.Request.PostRequest
import org.techtown.dailypicture.Retrofit.Response.PostResponse
import org.techtown.dailypicture.testRoom.Goal
import org.techtown.dailypicture.testRoom.GoalDao
import org.techtown.dailypicture.testRoom.GoalDatabase
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException


class AddGoalActivity: AppCompatActivity() {
    var GET_GALLERY_IMAGE:Int=200
    var goal= Goal()
    private var goalDatabase:GoalDatabase?=null
    var title:String?=null
    var thumbnail:String?=null
    var file: File?=null
    var imgDecodableString: String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_goal_2)

        //뒤로가기 버튼
        goal_back.setOnClickListener {
            var intent= Intent(this,MainActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            this.finish()
        }
        Toast.makeText(this, TokenTon.Token,Toast.LENGTH_LONG).show()

        //목표 등록 확인 버튼
        button_add.setOnClickListener {
            if(goal_input_add.length()==0){
                Toast.makeText(applicationContext,"목표 이름을 설정해주세요", Toast.LENGTH_LONG).show()
            }else {
                goal.goal_name=goal_input_add.text.toString()
                val database:GoalDatabase=GoalDatabase.getInstance(applicationContext)
                val goalDao: GoalDao =database.goalDao
                Thread{database.goalDao.insert(goal)}.start()
                //Toast.makeText(this,file.toString(),Toast.LENGTH_LONG).show()
                Toast.makeText(this,imgDecodableString.toString(),Toast.LENGTH_LONG).show()
                title=goal_input_add.text.toString();
                try {
                    PostServer(title.toString(), imgDecodableString.toString())
                }catch (e:Exception){
                    e.printStackTrace()
                    Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
                    Log.d("error",e.toString())
                }
                //var intent = Intent(this, MainActivity::class.java)
                //startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                //this.finish()

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


    //갤러리에서 이미지 불러오는 것
    //밑 SuppressLint는 에러나서 추가함
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            imageView_add.setImageURI(selectedImageUri)
            //Toast.makeText(this,selectedImageUri.toString(),Toast.LENGTH_LONG).show()

            //파일 경로 얻는 코드
            val filePathColumn =
                arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? =
                contentResolver.query(selectedImageUri!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            //이게 파일경로+파일명
            imgDecodableString = cursor.getString(columnIndex)
            //Toast.makeText(this,imgDecodableString,Toast.LENGTH_LONG).show()
            cursor.close()

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

    private fun PostServer(title:String,thumbnail:String){
        //Retrofit 서버 연결
        val file = File(thumbnail)
        val fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val part = MultipartBody.Part.createFormData("thumbnail", file.name, fileReqBody)

        val titleRequest=RequestBody.create(MediaType.parse("multipart/form-data"),title)

        //val postRequest=PostRequest(title,thumbnail,true)
        val call=RetrofitGenerator.create().registerPost(titleRequest,part,"Token "+TokenTon.Token)
        //val call=RetrofitGenerator.create().registerPost(postRequest,"Token "+TokenTon.Token)
        call.enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                //토큰 값 받아오기
                //Toast.makeText(this@AddGoalActivity,response.body()?.title.toString(),Toast.LENGTH_LONG).show()
                //TokenTon.set(response.body()?.token.toString())
            }
            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
            }
        })
    }

}
