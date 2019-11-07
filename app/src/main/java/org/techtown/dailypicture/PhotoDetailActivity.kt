package org.techtown.dailypicture

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.photo_detail_4.*
import org.techtown.dailypicture.testRoom.PictureDao
import org.techtown.dailypicture.testRoom.PictureDatabase
import java.io.File
import java.io.FileOutputStream
import java.util.*


class PhotoDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_detail_4)

        //선택한 사진정보 intent로 받아와서 보여주기
        var byteArray = getIntent().getByteArrayExtra("image")
        var image_id = getIntent().getIntExtra("image_id", 0)
        val picture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        detail_image.setImageBitmap(picture)

        //뒤로 가기 버튼
        back_detail.setOnClickListener {
            var intent = Intent(this, GoalDetailActivity::class.java)
            startActivityForResult(intent, 2)
            finish()
        }

        //삭제버튼
        delete_detail.setOnClickListener {
            val database: PictureDatabase = PictureDatabase.getInstance(applicationContext)
            val pictureDao: PictureDao = database.pictureDao
            Thread { database.pictureDao.delete(image_id) }.start()

            var intent = Intent(this, GoalDetailActivity::class.java)
            startActivityForResult(intent, 2)
            finish()
        }

        //저장버튼
        download_detail.setOnClickListener {
            // saveImageToInternalStorage(picture)
            saveImageToExternalStorage(picture)
            Toast.makeText(this, "갤러리에 저장되었습니다.", Toast.LENGTH_LONG).show()
        }

    }

    //갤러리로 저장
    private fun saveImageToExternalStorage(finalBitmap: Bitmap) {
        val root =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        //경로 지정
        val myDir = File("$root/Daily Picture")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "Image-$n.jpg"
        val file = File(myDir, fname)
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //미디어 스캐너로 바로 표시되도록 하는 부분
        MediaScannerConnection.scanFile(
            this, arrayOf(file.toString()), null
        ) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }

    }


}