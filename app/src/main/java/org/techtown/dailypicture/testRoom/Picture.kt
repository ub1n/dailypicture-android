package org.techtown.dailypicture.testRoom

import android.graphics.Bitmap
import androidx.room.*

@Entity
class Picture() { //저장할 정보, primaryKey가 필요
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var image: ByteArray?=null


}