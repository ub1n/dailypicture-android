package org.techtown.dailypicture.fragments

import android.graphics.Bitmap
import java.io.File

//비트맵이랑 사진 모드 담는 object
class ImageTon {
    companion object{

        lateinit var img: File
        lateinit var bm:Bitmap
        var filter:String?=null
        fun setfilter(fil:String?){
            filter=fil
        }
        var selfInt=0
        fun set(){
            selfInt=0
        }
    fun change(){
        if(selfInt==0)
            selfInt=1
        else
            selfInt=0
    }
        fun setBmp(bmp:Bitmap){
            bm=bmp
        }
        fun setImage(bmp:File){
            img=bmp
        }


    }
}