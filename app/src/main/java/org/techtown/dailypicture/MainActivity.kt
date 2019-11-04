package org.techtown.dailypicture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()  {
    //권한 요청을 위한 변수
    val multiplePermissionsCode=100
    val requiredPermissions=arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //카메라, 내장공간 사용 권한
        checkPermissions()

        goalAddbutton.setOnClickListener {
            var intent= Intent(this,AddGoalActivity::class.java)
            startActivityForResult(intent,2)
        }
        textView2.setOnClickListener {
            var intent=Intent(this,GoalDetailActivity::class.java)
            startActivityForResult(intent,2)
        }

        //setting 아이콘 클릭
        main_setting.setOnClickListener {
            var intent=Intent(this,SettingActivity::class.java)
            startActivityForResult(intent,2)
            finish()
        }
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


}
