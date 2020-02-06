package org.techtown.dailypicture

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.photo_detail_4.*
import org.techtown.dailypicture.Retrofit.Response.PostIdResponse
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class PhotoDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_detail_4)

        //선택한 사진정보 intent로 받아와서 보여주기
        var image = getIntent().getStringExtra("image")
        var image_id = getIntent().getIntExtra("image_id", 0)
        var image_days_count=getIntent().getIntExtra("image_days_count",0)
        goalText.setText("Day+"+image_days_count.toString())
        TokenTon.setimageId(image_id)
        /*val picture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        detail_image.setImageBitmap(picture)*/
        Picasso.get().load(image).into(detail_image)


        //뒤로 가기 버튼
        back_detail.setOnClickListener {
            var intent = Intent(this, GoalDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivityForResult(intent, 2)
            finish()
        }

        //삭제버튼
        delete_detail.setOnClickListener {
            Delete(image_id)

            var intent = Intent(this, GoalDetailActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)   //ABCDE에서 C 호출하면 ABC만 남김
            startActivityForResult(intent, 2)
            finish()
        }

        //저장버튼
        download_detail.setOnClickListener {
            //url로 이미지 저장시 Glide 이용
            Glide.with(this)
                .asBitmap()
                .load(image)
                .into(object : SimpleTarget<Bitmap>(1080, 1080) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        saveImage(resource)
                    }
                })
            Toast.makeText(this, "갤러리에 저장되었습니다.", Toast.LENGTH_LONG).show()
        }

    }

    /*
    //갤러리로 저장(서버 없을 때)
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
     */


    private fun Delete(id: Int?) {
        val call = RetrofitGenerator.create().imageDelete("Token " + TokenTon.Token, id)
        //val call=RetrofitGenerator.create().registerPost(postRequest,"Token "+TokenTon.Token)
        call.enqueue(object : Callback<PostIdResponse> {
            override fun onResponse(
                call: Call<PostIdResponse>?,
                response: Response<PostIdResponse>?
            ) {
                if(response?.isSuccessful==false)
                    ServerError()
            }

            override fun onFailure(call: Call<PostIdResponse>, t: Throwable) {
                ServerError()
            }
        })
    }
    private fun ServerError(){
        Toast.makeText(this,"서버와의 연결이 종료되었습니다.초기화면으로 돌아갑니다",Toast.LENGTH_LONG).show()

        val intent=Intent(this,LoadingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityForResult(intent,2)
        finish()
    }

    //갤러리 사진 저장
    internal fun saveImage(image: Bitmap) {
        val savedImagePath: String

        val imageFileName = System.currentTimeMillis().toString() + ".jpg"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ).toString() + "/DailyPicture"
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            MediaScannerConnection.scanFile(
                this, arrayOf(imageFile.toString()), null
            ) { path, uri ->
                Log.i("ExternalStorage", "Scanned $path:")
                Log.i("ExternalStorage", "-> uri=$uri")
            }
        }
    }


    private fun galleryAddPic(imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = FileProvider.getUriForFile(applicationContext, packageName, f)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)
    }


}