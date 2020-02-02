package org.techtown.dailypicture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
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
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.add_goal_2.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.techtown.dailypicture.Retrofit.Response.PostResponse
import org.techtown.dailypicture.testRoom.Goal
import org.techtown.dailypicture.testRoom.GoalDatabase
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.lang.System.out


class AddGoalActivity : AppCompatActivity() {
    var GET_GALLERY_IMAGE: Int = 200
    var goal = Goal()
    private var goalDatabase: GoalDatabase? = null
    var title: String? = null
    var thumbnail: String? = null
    var file: File? = null
    var imgDecodableString: String? = null

    var PIC_CROP: Int = 3;
    var PICK_IMAGE_REQUEST: Int = 2;
    var picUri: Uri? = null
    var imgStatus: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_goal_2)

        //뒤로가기 버튼
        goal_back.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            this.finish()
        }


        //목표 등록 확인 버튼
        button_add.setOnClickListener {
            if (goal_input_add.length() == 0) {
                Toast.makeText(applicationContext, "목표 이름을 설정해주세요", Toast.LENGTH_LONG).show()
            } else if (imgStatus == false) {
                Toast.makeText(applicationContext, "이미지를 추가해주세요", Toast.LENGTH_LONG).show()

            } else {
                /*Room 사용시 코드
                goal.goal_name=goal_input_add.text.toString()
                val database:GoalDatabase=GoalDatabase.getInstance(applicationContext)
                val goalDao: GoalDao =database.goalDao
                Thread{database.goalDao.insert(goal)}.start()
                 */
                title = goal_input_add.text.toString()
                try {
                    PostServer(title.toString(), imgDecodableString.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                    Log.d("error", e.toString())
                }
            }
        }

        //대표 사진 설정 부분
        imageView_add.setOnClickListener {
            /*//갤러리에서 불러오기
            var galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);*/
            ImagePicker()
        }
    }

    private fun ImagePicker(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("crop", "true")
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*if (resultCode == RESULT_OK) {
            //이미지 선택
            if (requestCode == PICK_IMAGE_REQUEST) {
                picUri = data?.getData()
                Log.d("uriGallery", picUri.toString())
                performCrop()
            }
            //이미지 자르기
            else if (requestCode == PIC_CROP) {
                //get the returned data
                val extras = data?.getExtras()
                //get the cropped bitmap
                val thePic = extras?.get("data") as Bitmap
                //자른 이미지 보여주기
                imageView_add.setImageBitmap(thePic)

                var bitmapToUri = getImageUri(this, thePic)
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? =
                    contentResolver.query(bitmapToUri!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                //이게 파일경로+파일명
                //저장하기 위해서 변수에 경로 넣기
                imgDecodableString = cursor.getString(columnIndex)

            }
        }*/
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            CropImage.activity(data.data!!).setAspectRatio(3,2).start(this)
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                var resultUri = result.getUri();
                imgStatus = true
                imageView_add.setImageURI(resultUri)
                var bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                var bitmapToUri = getImageUri(this, bitmap)
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? =
                    contentResolver.query(bitmapToUri!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                //이게 파일경로+파일명
                //저장하기 위해서 변수에 경로 넣기
                imgDecodableString = cursor.getString(columnIndex)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.getError()
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performCrop() {
        try {
            val cropIntent = Intent("com.android.camera.action.CROP")
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*")
            //set crop properties
            cropIntent.putExtra("crop", "true")
            //자르는 비율 설정
            cropIntent.putExtra("aspectX", 385)
            cropIntent.putExtra("aspectY", 250)
            //밖으로 출력될 때 비율
            cropIntent.putExtra("outputX", 385)
            cropIntent.putExtra("outputY", 250)
            //retrieve data on return
            cropIntent.putExtra("return-data", true)
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP)
            imgStatus = true
        } catch (anfe: ActivityNotFoundException) {
            val errorMessage = "크롭 할 수 없는 이미지 입니다."
            val toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    //Bitmap을 Uri로 변경하기
    private fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        var bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val titlename=Math.random()
        var path =
            MediaStore.Images.Media.insertImage(context.contentResolver, inImage,TokenTon.uuid.toString()+titlename.toString(), null)
        return Uri.parse(path)
    }

    /*
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
                //imageView_add.isClickable=true

            }

        }
    }     */


    //Uri를 ByteArray로 변환하는 함수
    fun convertImageToByte(uri: Uri): ByteArray? {
        var data: ByteArray? = null
        try {
            val cr = baseContext.contentResolver
            val inputStream = cr.openInputStream(uri)
            var bitmap = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            data = baos.toByteArray()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return data
    }

    private fun PostServer(title: String, thumbnail: String) {
        val file = File(thumbnail)
        val fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val part = MultipartBody.Part.createFormData("thumbnail", file.name, fileReqBody)
        val titleRequest = RequestBody.create(MediaType.parse("multipart/form-data"), title)
        //Toast.makeText(this,thumbnail,Toast.LENGTH_LONG).show()
        val call =
            RetrofitGenerator.create().registerPost(titleRequest, part, "Token " + TokenTon.Token)
        call.enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                if (response.isSuccessful == false) {
                    if (response.code() == 400) {
                        msgError()
                    } else {
                        ServerError()
                    }
                } else {
                    button_add.isEnabled = false
                    success()
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                ServerError()
            }
        })
    }

    private fun msgError() {
        Toast.makeText(this, "공백이 아닌 글자를 넣어주세요", Toast.LENGTH_LONG).show()
    }

    private fun ServerError() {

        Toast.makeText(this, "서버와의 연결이 종료되었습니다.초기화면으로 돌아갑니다", Toast.LENGTH_LONG).show()

        val intent = Intent(this, LoadingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityForResult(intent, 2)
        finish()
    }

    private fun success() {
        var intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        this.finish()
    }


}
