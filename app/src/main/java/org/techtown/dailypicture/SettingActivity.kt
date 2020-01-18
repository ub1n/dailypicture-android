package org.techtown.dailypicture

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings.*
import org.techtown.dailypicture.alarm.ReminderWorker

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val deviceWidth = resources.displayMetrics.widthPixels
        setting_back.setWidth(deviceWidth)
        //앱 사용방법 안내
        textView21.setOnClickListener{
            var intent=Intent(this,Activity_info::class.java)
            intent.putExtra("number",2)
            startActivity(intent)
        }
        //뒤로가기 버튼
        setting_back.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        if(android.os.Build.VERSION.SDK_INT>=26){
            //알람 상태 유지
            val alarm=getSharedPreferences("alarm",Context.MODE_PRIVATE)
            val alarmEditor=alarm.edit()
            val alarm_set=alarm.getString("alarm","")

            //이전에 알람 설정한 것에 대한 처리
            if(alarm_set=="alarm_on") {
                switch1.setChecked(true)
                ReminderWorker.runAt()
            }else if(alarm_set=="alarm_off"){
                switch1.setChecked(false)
                ReminderWorker.cancel()
            }else{
                ReminderWorker.cancel()
            }





            //알람 설정 여부 스위치
            switch1.setOnCheckedChangeListener{buttonView,isChecked ->
                if(isChecked){
                    //on_off.setText("ON")
                    Toast.makeText(this,"알림이 설정됩니다.",Toast.LENGTH_LONG).show()
                    alarmEditor.putString("alarm","alarm_on")
                    alarmEditor.commit()
                    //알람 켜기
                    ReminderWorker.runAt()
                }
                else{
                    //on_off.setText("OFF")
                    Toast.makeText(this,"알림이 해제되었습니다.",Toast.LENGTH_LONG).show()
                    alarmEditor.putString("alarm","alarm_off")
                    alarmEditor.commit()
                    //알람 끄기
                    ReminderWorker.cancel()
                }
            }
        }else{
            switch1.isClickable=false
        }


    }


}