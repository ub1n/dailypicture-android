package org.techtown.dailypicture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import org.techtown.dailypicture.Retrofit.Request.LoginRequest
import org.techtown.dailypicture.Retrofit.Request.RegisterRequest
import org.techtown.dailypicture.Retrofit.Response.LoginResponse
import org.techtown.dailypicture.Retrofit.Response.RegisterResponse
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//맨 처음 로딩 페이지. uuid주고 토큰 값 얻어오는 것 해야함
class LoadingActivity : AppCompatActivity() {
    val DURATION:Long=1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        var uuid = intent.getStringExtra("uuid")
        if(uuid==null){
            uuid=TokenTon.uuid
        }
        val status = intent.getIntExtra("status", 0)
        //  TokenTon.setuuid(uuid)     //얘 쓰면 최초실행시 토큰값이상해짐
        if (uuid != "" && uuid != null) {

            TokenTon.setuuid(uuid)
        }
            //Toast.makeText(this,TokenTon.uuid,Toast.LENGTH_LONG).show()
        if (status == 0) {
            LoginServer(uuid, uuid)
        }

        //RegisterServer(uuid,uuid)
        /*Handler().postDelayed({
            val intent= Intent(this,MainActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        },DURATION)*/


    }
    private fun LoginServer(username:String,password:String){
        //Retrofit 서버 연결
        val loginRequest= LoginRequest(username,password)
        val call=RetrofitGenerator.create().getToken(loginRequest)
        val intent = Intent(this, MainActivity::class.java)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                //토큰 값 받아오기
                //Toast.makeText(this@MainActivity,response.body()?.token.toString(),Toast.LENGTH_LONG).show()
                if(response.isSuccessful) {

                    TokenTon.set(response.body()?.token.toString())
                    success()
                }
                //PostGetServer()
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                ServerError()
            }
        })
    }
    private fun ServerError(){
        Toast.makeText(this,"서버와의 연결이 종료되었습니다. 연결 상태를 확인해주세요",Toast.LENGTH_LONG).show()
    }
    private fun success(){
        Handler().postDelayed({
            val intent= Intent(this,MainActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        },DURATION)
    }





}
