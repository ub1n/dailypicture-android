package org.techtown.dailypicture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import org.techtown.dailypicture.Retrofit.Request.RegisterRequest
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

        val uuid=intent.getStringExtra("uuid")
      //  TokenTon.setuuid(uuid)
        if(uuid!=""&&uuid!=null){
            TokenTon.setuuid(uuid)
        }
        Toast.makeText(this,TokenTon.uuid,Toast.LENGTH_LONG).show()

        //RegisterServer(uuid,uuid)
        Handler().postDelayed({
            val intent= Intent(this,MainActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        },DURATION)
    }





}
