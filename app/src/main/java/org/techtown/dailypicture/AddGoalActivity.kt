package org.techtown.dailypicture

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.add_goal_2.*
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import org.techtown.dailypicture.testRoom.Goal
import org.techtown.dailypicture.testRoom.GoalDao
import org.techtown.dailypicture.testRoom.GoalDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.ContentResolver
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


class AddGoalActivity: AppCompatActivity() {
    var GET_GALLERY_IMAGE:Int=200
    var goal= Goal()
    private var goalDatabase:GoalDatabase?=null


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

            var intent= Intent(this,MainActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            this.finish()
        }

        //대표 사진 설정 부분
        imageView_add.setOnClickListener {
            var intent=Intent(Intent.ACTION_PICK);
            intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")
            startActivityForResult(intent,GET_GALLERY_IMAGE)
        }
    }

    //갤러리에서 이미지 불러오는 것
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            imageView_add.setImageURI(selectedImageUri)

            goal.goal_name=goal_input_add.toString()
            //갤러리에서 이미지 불러오는건 Uri, Room에 넣는 건 ByteArray이므로 변환필요
            if (selectedImageUri != null) {
                goal.image=convertImageToByte(selectedImageUri)
            }
            val database:GoalDatabase=GoalDatabase.getInstance(applicationContext)
            val goalDao: GoalDao =database.goalDao

            Thread{database.goalDao.insert(goal)}.start()
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
