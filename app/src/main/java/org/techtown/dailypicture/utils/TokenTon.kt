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
        var imageId:Int?=null
        fun setimageId(id:Int){
            imageId=id
        }
        var videopath:String?=null
        fun setvideoPath(Path:String){
            videopath=Path;
        }
    }
}