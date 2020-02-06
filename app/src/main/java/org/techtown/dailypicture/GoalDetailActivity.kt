package org.techtown.dailypicture

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.add_goal_2.*
import kotlinx.android.synthetic.main.add_photo_dialog.view.*
import kotlinx.android.synthetic.main.goal_detail_3.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.techtown.dailypicture.Retrofit.Response.ImagePostResponse
import org.techtown.dailypicture.Retrofit.Response.PostIdResponse
import org.techtown.dailypicture.Retrofit.Response.images
import org.techtown.dailypicture.adapter.DetailAdapter
import org.techtown.dailypicture.fragments.ImageTon
import org.techtown.dailypicture.testRoom.*
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class GoalDetailActivity: AppCompatActivity() { //여긴 싹다 임시(recyclerview 테스트용)
    var picture= Picture()
    private var pictureDatabase:PictureDatabase?=null
    private var pictureList=listOf<Picture>()
    private var imageList=listOf<images>()
    lateinit var mAdapter:DetailAdapter
    var goal= Goal()
    var goalname=""
    var count_all_image:String?=null
    var res: Resources?=null
    var count:Int?=null
    var file: File? = null
    var imgDecodableString: String? = null

    var PIC_CROP: Int = 51;
    var PICK_IMAGE_REQUEST: Int = 52;
    var picUri: Uri? = null
    var imgStatus: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goal_detail_3)
        ImageTon.setfilter(null)
        //목표 이름 불러오기
        var goal_name=getIntent().getStringExtra("goal_name")
        var goal_id=getIntent().getIntExtra("goal_id",0)
        //TokenTon.setpostId(goal_id)
        //goalText.setText(goal_name)
        try{
        PostIdGetServer()}catch(e:Exception){
            //Toast.makeText(this,"$e",Toast.LENGTH_LONG).show()
        }
       // Toast.makeText(this,"${TokenTon.postId}",Toast.LENGTH_LONG).show()
        pictureDatabase=PictureDatabase.getInstance(this)

        //이미지 보여주는 recyclerview
        mAdapter= DetailAdapter(applicationContext)
        val r = Runnable {   //recyclerview와 android room 결합
            try {
                //pictureList = pictureDatabase?.pictureDao?.getPicture()!!
                mAdapter = DetailAdapter(applicationContext)
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
        cameraButton.setOnClickListener {
            val mDialogView=LayoutInflater.from(this).inflate(R.layout.add_photo_dialog,null)
            val mBuilder=AlertDialog.Builder(this)
                .setView(mDialogView)
            val mAlertDialog=mBuilder.show()
            //사진찍기

            mDialogView.add_camera_lay.setOnClickListener{
                mAlertDialog.dismiss()
                //var intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                var intent=Intent(this,CameraActivity::class.java)
                startActivityForResult(intent,123)

            }
            //갤러리 불러오기
            mDialogView.add_gallery_lay.setOnClickListener {
                mAlertDialog.dismiss()
                /*var galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
            */
            ImagePicker()
            }



        }
        //내보내기 버튼
        gifbutton.setOnClickListener {
            if(mAdapter.itemCount<10){ //여기 카운트 갯수 바꾸면 사진갯수 조절
                Toast.makeText(this,"사진 10장부터 영상 변환이 가능해요!",Toast.LENGTH_LONG).show()
            }else{
            var intent=Intent(this,GifActivitiy::class.java)
                intent.putExtra("goal_name",goalname)
            startActivityForResult(intent,3)}
        }
        //뒤로가기 버튼
        back_goal_detail.setOnClickListener{
            var intent=Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(intent,3)
            finish()
        }
        //삭제하기 버튼
        delete_goal_detail.setOnClickListener {
            //goal 테이블에서 목표 delete
            val database: GoalDatabase = GoalDatabase.getInstance(applicationContext)
            val goalDao: GoalDao =database.goalDao
            Thread{database.goalDao.delete(goal_id)}.start()
            Delete()
            var intent=Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(intent,3)
        }

    }

    private fun ImagePicker(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("crop", "true")
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
   }

    override fun onRestart() {
        super.onRestart()
        PostIdGetServer()
    }

    override fun onResume() {
        super.onResume()
        PostIdGetServer()
    }


    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*if (resultCode == RESULT_OK) {
           Log.d("requestCode",requestCode.toString())
            //이미지 선택
            if (requestCode == PICK_IMAGE_REQUEST) {
                Toast.makeText(this,"저장완료44",Toast.LENGTH_LONG).show()
                picUri = data?.getData()
                Log.d("uriGallery", picUri.toString())
                performCrop()
            }
            //이미지 자르기
            else if (requestCode == PIC_CROP) {
                Log.d("requestCode",requestCode.toString())

                //get the returned data
                val extras = data?.getExtras()
                //get the cropped bitmap
                val thePic = extras?.get("data") as Bitmap
                //자른 이미지 보여주기
                //imageView_add.setImageBitmap(thePic)


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
                PostImage(imgDecodableString.toString())
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }
            CropImage.activity(data.data!!).setAspectRatio(1,1).start(this)
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                var resultUri = result.getUri();
                //Toast.makeText(this,resultUri.toString(),Toast.LENGTH_LONG);
                //Toast.makeText(this,resultUri.toString(),Toast.LENGTH_LONG).show()
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
                PostImage(imgDecodableString.toString())
                Thread.sleep(200);
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
            cropIntent.putExtra("outputX", 1080)
            cropIntent.putExtra("outputY", 1080)
            //자르는 비율 설정
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            //밖으로 출력될 때 비율

            //retrieve data on return
            cropIntent.putExtra("scaleUpIfNeeded",true)
            cropIntent.putExtra("scale",true)
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT,picUri)
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
            MediaStore.Images.Media.insertImage(context.contentResolver, inImage,"goalImage"+titlename, null)
        return Uri.parse(path)
    }

    var code:Int=1
    private fun PostIdGetServer(){
        //Retrofit 서버 연결
        val call= RetrofitGenerator.create().postIdPost("Token "+TokenTon.Token,TokenTon.postId)
        //val call=RetrofitGenerator.create().registerPost(postRequest,"Token "+TokenTon.Token)
        call.enqueue(object : Callback<PostIdResponse> {
            override fun onResponse(call: Call<PostIdResponse>?, response: Response<PostIdResponse>?) {
                if(response?.isSuccessful==false){
                    if(response?.code()==404){
                        code=404}else{
                        ServerError()
                    }
                }else {
                    //Toast.makeText(this@GoalDetailActivity,response?.body()?.title,Toast.LENGTH_LONG).show()
                    goalText.setText(response?.body()?.title)
                    //ddayText.setText("D+"+response?.body()?.dday.toString())
                    goalname = response?.body()?.title.toString()
                    if (response?.body()?.images != null) {
                            //imageList = response?.body()?.images!!
                            mAdapter.setGoalListItems(response.body()?.images!!)
                    }
                    count = mAdapter.itemCount
                    //사진 없으면 안내 글자 보이기
                    if(count==0){
                        detailRecyclerView.visibility= View.GONE
                        textView18.visibility=View.VISIBLE
                        textView19.visibility=View.VISIBLE
                        textView20.visibility=View.VISIBLE
                    }else{
                        detailRecyclerView.visibility= View.VISIBLE
                        textView18.visibility=View.GONE
                        textView19.visibility=View.GONE
                        textView20.visibility=View.GONE
                    }
                }
                //count_all_image=String.format(resources.getString(R.string.count_image),2,mAdapter.itemCount.toString())

            }
            override fun onFailure(call: Call<PostIdResponse>, t: Throwable) {
                if(code!=404){
                    ServerError()
                }
                //Toast.makeText(this@GoalDetailActivity,"$t",Toast.LENGTH_LONG).show()

            }
        })
    }
    private fun Delete(){
        val call= RetrofitGenerator.create().postIdDelete("Token "+TokenTon.Token,TokenTon.postId)
        //val call=RetrofitGenerator.create().registerPost(postRequest,"Token "+TokenTon.Token)
        call.enqueue(object : Callback<PostIdResponse> {
            override fun onResponse(call: Call<PostIdResponse>?, response: Response<PostIdResponse>?) {
                if(response!!.isSuccessful==false){
                    ServerError()
                }
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


    private fun PostImage(img:String) {
        //Retrofit 서버 연결
        val file = File(img)
        val fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val part = MultipartBody.Part.createFormData("url", file.name, fileReqBody)
        val titleRequest =
            RequestBody.create(MediaType.parse("multipart/form-data"), TokenTon.postId.toString())

        val call =
            RetrofitGenerator.create().imagePost(titleRequest, part, "Token " + TokenTon.Token)

        call.enqueue(object : Callback<ImagePostResponse> {
            override fun onResponse(
                call: Call<ImagePostResponse>,
                response: Response<ImagePostResponse>) {
                //file.delete()
                //토큰 값 받아오기
                //Toast.makeText(this@AddGoalActivity,response.body()?.title.toString(),Toast.LENGTH_LONG).show()
                //TokenTon.set(response.body()?.token.toString())
                if (response.isSuccessful == false) {
                    ServerError()
                }
            }

            override fun onFailure(call: Call<ImagePostResponse>, t: Throwable) {
            }
        })
    }
}