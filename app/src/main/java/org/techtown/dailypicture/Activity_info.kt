package org.techtown.dailypicture

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.info_slider.view.*

class Activity_info : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        var num=intent.getIntExtra("number",0)

        try{
        val adapter=pageadapter(num)
        viewpager.adapter=adapter}catch(e:Exception){
            Toast.makeText(this,"$e",Toast.LENGTH_LONG).show()
        }

        info_back.setOnClickListener {
            if (num == 2) {
                finish()
            } else {
                finish()
            }
        }


    }
    fun cancel2(){
        val intent= Intent(this,MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    class pageadapter(num:Int):PagerAdapter(){
        val images_first= intArrayOf(R.drawable.sample0,R.drawable.sample1,R.drawable.sample2,R.drawable.sample3,R.drawable.sample4)
        val images= intArrayOf(R.drawable.sample0,R.drawable.sample1,R.drawable.sample2,R.drawable.sample3,R.drawable.sample4)
        val numb=num
        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view==`object`

        }
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(container.context)
            val view = inflater.inflate(R.layout.info_slider, container, false)

            view.info_imageView.setImageResource(images[position])
            view.count_button.text="  ${position+1} / 5  "
/*            view.info_back2.setOnClickListener{
                if(numb==2){
                    val intent= Intent(view.context,SettingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    view.context.startActivity(intent)}
                else{
                    val intent=Intent(view.context,StartAppActivity::class.java)
                    intent.putExtra("check",2)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    view.context.startActivity(intent)
                }
            }*/

            container.addView(view)
            return view
        }
        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View?)
        }

    }

}
