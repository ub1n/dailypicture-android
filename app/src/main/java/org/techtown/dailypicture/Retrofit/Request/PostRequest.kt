package org.techtown.dailypicture.Retrofit.Request

import java.io.File

data class PostRequest(val title:String, val thumbnail: File, val status:Boolean)