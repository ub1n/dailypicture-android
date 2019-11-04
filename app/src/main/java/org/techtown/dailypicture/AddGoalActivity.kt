package org.techtown.dailypicture

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.add_goal_2.*

class AddGoalActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_goal_2)

        //뒤로가기 버튼
        goal_back.setOnClickListener {
            var intent= Intent(this,MainActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            this.finish()
        }

        //목표 등록 확인 버튼
        button_add.setOnClickListener {
            var intent= Intent(this,MainActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            this.finish()
        }
    }
}
