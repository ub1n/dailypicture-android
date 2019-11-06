package org.techtown.dailypicture

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings.*
import org.techtown.dailypicture.alarm.ReminderWorker

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)


        //뒤로가기 버튼
        setting_back.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        switch1.setOnCheckedChangeListener{buttonView,isChecked ->
            if(isChecked){
                on_off.setText("ON")
                //알람 켜기
                ReminderWorker.runAt()
            }
            else{
                on_off.setText("OFF")
                //알람 끄기
                ReminderWorker.cancel()
            }
        }

    }


}