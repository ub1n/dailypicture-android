package org.techtown.dailypicture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goalAddbutton.setOnClickListener {
            var intent= Intent(this,AddGoalActivity::class.java)
            startActivityForResult(intent,2)
        }
        textView2.setOnClickListener {
            var intent=Intent(this,GoalDetailActivity::class.java)
            startActivityForResult(intent,2)
        }
    }



}
