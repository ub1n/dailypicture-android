package org.techtown.dailypicture.testRoom

import androidx.room.*

@Dao
interface PictureDao {
    @Query("SELECT * FROM picture ") //email 값이 등록한 email과 같은 유저 전부 보이는 쿼리
    fun getPicture(): List<Picture>

    @Query("DELETE FROM picture WHERE id = (:id)")
    fun delete(id:Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE) //정보 넣기
    fun insert(picture:Picture)




}