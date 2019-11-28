package org.techtown.dailypicture.utils

class TokenTon {
    companion object{
        var Token:String=""
        var uuid:String=""
        fun set(Tok:String){
            Token=Tok
        }
        fun setuuid(id:String){
            uuid=id
        }
        var postId:Int?=null
        fun setpostId(id:Int){
            postId=id
        }
    }
}