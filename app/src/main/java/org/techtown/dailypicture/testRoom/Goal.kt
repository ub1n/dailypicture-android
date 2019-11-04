package org.techtown.dailypicture.testRoom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal")
class Goal(
    @PrimaryKey(autoGenerate=true)var id:Int=0,
    var goal_name:String?=null,
    var image: ByteArray?=null){
    constructor():this(0,"",null)
}