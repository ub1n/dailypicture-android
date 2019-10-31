package org.techtown.dailypicture

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.add_goal_2.*

class AddGoalActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_goal_2)

        button_add.setOnClickListener {
            var intent= Intent(this,MainActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            this.finish()
        }
    }
}
