package org.techtown.dailypicture

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_goal_2.*
import kotlinx.android.synthetic.main.detail_item_view.*
import kotlinx.android.synthetic.main.goal_detail_3.*
import org.techtown.dailypicture.Retrofit.Response.PostIdResponse
import org.techtown.dailypicture.Retrofit.Response.PostResponse
import org.techtown.dailypicture.Retrofit.Response.images
import org.techtown.dailypicture.adapter.DetailAdapter
import org.techtown.dailypicture.adapter.MainAdapter
import org.techtown.dailypicture.testRoom.*
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goal_detail_3)

        /*if(TokenTon.uuid==""||TokenTon.Token==""){
            var intent=Intent(this, LoadingActivity::class.java)
            startActivityForResult(intent,2)
            finish()
        }*/

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
        cameraButton.setOnClickListener { //임시
            //var intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            var intent=Intent(this,CameraActivity::class.java)
            startActivityForResult(intent,123)
        }
        //내보내기 버튼
        gifbutton.setOnClickListener {
            if(mAdapter.itemCount<20){ //여기 카운트 갯수 바꾸면 사진갯수 조절
                Toast.makeText(this,"사진 20장부터 영상 변환이 가능해요!",Toast.LENGTH_LONG).show()
                //Toast.makeText(this,"사진의 수가 적습니다. 사진을 20장 이상 찍어주세요",Toast.LENGTH_LONG).show()
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

    override fun onRestart() {
        super.onRestart()
        PostIdGetServer()
    }

    override fun onResume() {
        super.onResume()
        PostIdGetServer()
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
    var code:Int=3
    private fun PostIdGetServer(){
        //Retrofit 서버 연결
        val call= RetrofitGenerator.create().postIdPost("Token "+TokenTon.Token,TokenTon.postId)
        //val call=RetrofitGenerator.create().registerPost(postRequest,"Token "+TokenTon.Token)
        call.enqueue(object : Callback<PostIdResponse> {
            override fun onResponse(call: Call<PostIdResponse>?, response: Response<PostIdResponse>?) {
                if(response?.isSuccessful==false){
                    if(response?.code()==404){
                        code=404}else{
                    ServerError()}
                }else {
                    //Toast.makeText(this@GoalDetailActivity,response?.body()?.title,Toast.LENGTH_LONG).show()
                    goalText.setText(response?.body()?.title)
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
                ServerError()}
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
}