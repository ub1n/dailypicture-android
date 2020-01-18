package org.techtown.dailypicture

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start_app.*
import org.techtown.dailypicture.Retrofit.Request.RegisterRequest
import org.techtown.dailypicture.Retrofit.Response.RegisterResponse
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StartAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_app)
        var check = intent.getIntExtra("check", 0)
        /*if(check==0){

        }*/
        var terms_agree_3: Int = 0 //전체 동의

        //글씨 밑에 밑줄
        text_require1.setOnClickListener {
            var intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.notion.so/prographyoreo/2c09c40094ed46cb9930718678a41e93")
            )
            startActivity(intent)
        }
        text_require1.getPaint().setUnderlineText(true)
        //text_require2.getPaint().setUnderlineText(true)

        //이전에 실행한 기록 있는지 SharedPreference로 저장
        val save = getSharedPreferences("save", Context.MODE_PRIVATE)
        val saveEditor = save.edit()
        val terms_agree_4 = save.getString("agree", "")

        val uuidSP = getSharedPreferences("uuid", Context.MODE_PRIVATE)
        val uuidEditor = uuidSP.edit()
        val getuuid = uuidSP.getString("uuid", "")
        val uuid = getUuid()
        TokenTon.setuuid(uuid)

        //이전 실행기록이 있는지 확인하는 것
        if (terms_agree_4 == "all agree" && getuuid != null) {
            val intent = Intent(this, LoadingActivity::class.java)
            //Toast.makeText(this,getuuid,Toast.LENGTH_LONG).show()
            intent.putExtra("uuid", getuuid)
            startActivity(intent)
            finish()
        } else {
            var intent = Intent(this, Activity_info::class.java)
            startActivity(intent)
            //전체동의 체크박스
            checkBox3.setOnClickListener(View.OnClickListener {
                terms_agree_3 = 1
            }
            )

            //시작하기 버튼
            btn_start.setOnClickListener {
                if (terms_agree_3 == 1) {
                    saveEditor.putString("agree", "all agree")
                    saveEditor.commit()

                    RegisterServer(uuid, uuid)
                    uuidEditor.putString("uuid", uuid)
                    uuidEditor.commit()

                    //user.uuid=uuid
                    //val database:UserDatabase=UserDatabase.getInstance(applicationContext)
                    //val userDao: UserDao =database.userDao
                    //Thread{database.userDao.insert(user)}.start()

                    var intent = Intent(this, LoadingActivity::class.java)
                    intent.putExtra("uuid", getuuid)
                    intent.putExtra("status", 1)

                    //uuid를 전달해준다. 이 값을 기억해야함!
                    //intent.putExtra("uuid",uuid);
                    startActivityForResult(intent, 2)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "약관을 체크해주세요", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    //UUID값 받아오기
    private fun getUuid(): String {
        //UUID를 생성하지만 랜덤으로 생성되기 때문에 내부 저장소에 저장해두어야함
        //사용자가 내부저장소를 지우거나 앱을 삭제 후 재설치하는 경우 ID가 달라질 수 있음
        val uuid = UUID.randomUUID().toString()

        return uuid
    }

    //Retrofit 서버 연결
    private fun RegisterServer(username: String, password: String) {
        val userRequest = RegisterRequest(username)
        val call = RetrofitGenerator.create().registerUser(userRequest)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                //토큰 값 받아오기
                //Toast.makeText(this@StartAppActivity,response.body()?.uuid.toString(), Toast.LENGTH_LONG).show()
                //TokenTon.setuuid(response.body()?.uuid.toString())

            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {

            }

        })

    }


}
