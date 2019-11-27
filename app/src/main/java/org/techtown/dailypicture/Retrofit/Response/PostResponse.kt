package org.techtown.dailypicture.Retrofit.Response

data class PostResponse(var id:Int,var user_id:String,var title:String,var thumbnail:String?,var status:String,var images:List<String>)