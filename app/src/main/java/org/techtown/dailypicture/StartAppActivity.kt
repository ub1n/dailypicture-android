package org.techtown.dailypicture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_start_app.*

class StartAppActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_app)


        var terms_agree_1:Int=0 //이용약관 .체크되면 1,아니면 0
        var terms_agree_2:Int=0 //개인정보동의
        var terms_agree_3:Int=0 //전체 동의


        //이용약관 체크박스
        checkBox1.setOnClickListener(View.OnClickListener {
                if (checkBox1.isChecked) {
                    terms_agree_1=1;
                }
                else{
                    terms_agree_1=0;
                }
            }
        )
        //개인정보 동의 체크박스
        checkBox2.setOnClickListener(View.OnClickListener {
                if (checkBox2.isChecked) {
                    terms_agree_2=1;
                }
                else{
                    terms_agree_2=0;
                }
            }
        )
        //전체동의 체크박스
        checkBox3.setOnClickListener(View.OnClickListener {
                if (checkBox3.isChecked) {
                    terms_agree_3=1;
                    checkBox1.setChecked(true)
                    checkBox2.setChecked(true)
                }
                else{
                    terms_agree_3=0;
                    checkBox1.setChecked(false)
                    checkBox2.setChecked(false)
                }
            }
        )


        //시작하기 버튼
        btn_start.setOnClickListener{
            if(terms_agree_3 !=1){
                if(terms_agree_2==1){
                    if(terms_agree_1==1){
                        var intent=Intent(this,MainActivity::class.java)
                        startActivityForResult(intent,2)
                        finish()
                    }else{
                        Toast.makeText(applicationContext,"약관을 체크해주세요",Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(applicationContext,"약관을 체크해주세요",Toast.LENGTH_LONG).show()
                }

            }
            //전체약관 체크된 경우
            else{
                var intent=Intent(this,MainActivity::class.java)
                startActivityForResult(intent,2)
                finish()
            }
        }
    }





}
