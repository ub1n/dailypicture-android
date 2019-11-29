package org.techtown.dailypicture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.techtown.dailypicture.Retrofit.Request.LoginRequest
import org.techtown.dailypicture.Retrofit.Response.LoginResponse
import org.techtown.dailypicture.Retrofit.Response.PostResponse
import org.techtown.dailypicture.adapter.MainAdapter
import org.techtown.dailypicture.testRoom.*
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import java.io.File
import java.lang.Exception
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity()  {
    var goal= Goal()
    private var goalDatabase:GoalDatabase?=null
    private var goalList:List<PostResponse> = listOf()
    lateinit var mAdapter:MainAdapter



    //권한 요청을 위한 변수
    val multiplePermissionsCode=100
    val requiredPermissions=arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(TokenTon.uuid !=null && TokenTon.uuid !="") {
            if(TokenTon.Token==""||TokenTon.Token==null){
                LoginServer(TokenTon.uuid, TokenTon.uuid)

            }
        }
       // var intent= this!!.getIntent()
        //var uuid=intent.getStringExtra("uuid");
//        Log.d("uuid",uuid.toString());

        //목표 사진 보여주기
        goalDatabase= GoalDatabase.getInstance(this)
        mAdapter= MainAdapter(applicationContext)

        //item 사이에 줄 만들기
        mainRecyclerView.addItemDecoration(
            DividerItemDecoration(this,DividerItemDecoration.VERTICAL)
        )

        PostGetServer()
        val r= Runnable {
            try{
                //goalList=goalDatabase?.goalDao?.getGoal()!!
                mAdapter= MainAdapter(applicationContext)
                mAdapter.notifyDataSetChanged()

                mainRecyclerView.adapter=mAdapter
                mainRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                mainRecyclerView.setHasFixedSize(true)
            }catch (e:Exception){
                Log.d("tag", "Error - $e")
            }
        }

        val thread=Thread(r)
        thread.start()

        //카메라, 내장공간 사용 권한
        checkPermissions()

        //목표 추가버튼
        goalAddbutton.setOnClickListener {
            //여기에 임시로 서버 연결 써놓음
           // LoginServer(uuid,uuid)
            var intent= Intent(this,AddGoalActivity::class.java)
            startActivityForResult(intent,2)
        }


        //setting 아이콘 클릭
        main_setting.setOnClickListener {
            var intent=Intent(this,SettingActivity::class.java)
            startActivityForResult(intent,2)
        }
    }

    override fun onRestart() {
        super.onRestart()
        PostGetServer()
    }


    private fun PostGetServer(){
        //Retrofit 서버 연결
        //val postRequest=PostRequest(title,thumbnail,true)
        val call=RetrofitGenerator.create().getPost("Token "+TokenTon.Token)
        //val call=RetrofitGenerator.create().registerPost(postRequest,"Token "+TokenTon.Token)
        call.enqueue(object : Callback<List<PostResponse>> {
            override fun onResponse(call: Call<List<PostResponse>>?, response: Response<List<PostResponse>>?) {
                //토큰 값 받아오기
                //Toast.makeText(this@AddGoalActivity,response.body()?.title.toString(),Toast.LENGTH_LONG).show()
                //TokenTon.set(response.body()?.token.toString())
                // )
                try{
                mAdapter.setGoalListItems(response?.body()!!)}catch(e:Exception){
                //    Toast.makeText(this@MainActivity,"$e",Toast.LENGTH_LONG).show()
                }
                if(response?.body() != null) {
                  //  Toast.makeText(this@MainActivity,response.body()!![0].title,Toast.LENGTH_LONG).show()
                    mAdapter.setGoalListItems(response.body()!!)

                }
            }
            override fun onFailure(call: Call<List<PostResponse>>, t: Throwable) {
                Toast.makeText(this@MainActivity,"$t",Toast.LENGTH_LONG).show()
            }
        })
    }



    //퍼미션 체크 및 권한 요청 함수
    private fun checkPermissions(){
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList=ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면
        if(rejectedPermissionList.isNotEmpty()){
            val array= arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this,rejectedPermissionList.toArray(array),multiplePermissionsCode)
        }
    }

    //권한 요청 결과 함수
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            multiplePermissionsCode -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i("TAG", "The user has denied to $permission")
                            Log.i("TAG", "I can't work for you anymore then. ByeBye!")
                            val builder= AlertDialog.Builder(this)
                            val dialogView=layoutInflater.inflate(R.layout.permission_popup,null)
                            builder.setView(dialogView)
                                .setPositiveButton("확인"){dialogInterface, i ->
                                    finish()
                                }.show()
                        }
                    }
                }
            }
        }
    }

    //uuid값 전달하고 토큰 값 받아오기
    private fun LoginServer(username:String,password:String){
        //Retrofit 서버 연결
        val loginRequest=LoginRequest(username,password)
        val call=RetrofitGenerator.create().getToken(loginRequest)
        val intent = Intent(this, MainActivity::class.java)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                //토큰 값 받아오기
                //Toast.makeText(this@MainActivity,response.body()?.token.toString(),Toast.LENGTH_LONG).show()
                TokenTon.set(response.body()?.token.toString())
                //PostGetServer()
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

            }
        })
    }

}
