package org.techtown.dailypicture.Retrofit.Response

data class PostIdResponse(var id:Int,var user_id:String,var title:String,var thumbnail:String?,var status:Boolean,var images:List<images>)