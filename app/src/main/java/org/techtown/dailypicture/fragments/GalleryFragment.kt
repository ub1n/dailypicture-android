/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.techtown.dailypicture.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import java.io.File
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.media.Image
import android.media.ImageReader
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraX
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.camera_ui_container.*
import kotlinx.android.synthetic.main.camera_ui_container.view.*
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_gallery.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.techtown.dailypicture.*
/*import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc*/
import org.techtown.dailypicture.utils.showImmersive
import org.techtown.dailypicture.utils.padWithDisplayCutout
import org.techtown.dailypicture.Retrofit.Response.ImagePostResponse
import org.techtown.dailypicture.Retrofit.Response.PostResponse
import org.techtown.dailypicture.testRoom.Picture
import org.techtown.dailypicture.testRoom.PictureDao
import org.techtown.dailypicture.testRoom.PictureDatabase
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

//import org.techtown.dailypicture.frgments.GalleryFragmentArgs

val EXTENSION_WHITELIST = arrayOf("JPG")

/** Fragment used to present the user with a gallery of photos taken */
class GalleryFragment internal constructor() : Fragment() {
    var pic = Picture()
    /** AndroidX navigation arguments */
    private val args: GalleryFragmentArgs by navArgs()

    private lateinit var mediaList: MutableList<File>

    /** Adapter class used to present a fragment containing one photo or video as a page */
    inner class MediaPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = mediaList.size
        override fun getItem(position: Int): Fragment = PhotoFragment.create(mediaList[position])
        override fun getItemPosition(obj: Any): Int = POSITION_NONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mark this as a retain fragment, so the lifecycle does not get restarted on config change
        retainInstance = true

        // Get root directory of media from navigation arguments
        val rootDirectory = File(args.rootDirectory)

        // Walk through all files in the root directory
        // We reverse the order of the list to present the last photos first
        mediaList = rootDirectory.listFiles { file ->
            EXTENSION_WHITELIST.contains(file.extension.toUpperCase())
        }.sorted().reversed().toMutableList()
        if(mediaList.isEmpty()){
            fragmentManager?.popBackStack()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //var bm: Bitmap = BitmapFactory.decodeFile(mediaList[0].path)
        //var bm:Bitmap=BitmapFactory.decodeFile(ImageTon.img.absolutePath)
        val matrix = Matrix()
        if (ImageTon.selfInt==1) {
            //matrix.postRotate(270f)
            //matrix.postRotate(90f)
            matrix.setScale(-1.0f, 1.0f)
            matrix.preRotate(270f)
        }else if(ImageTon.selfInt==0){
            matrix.postRotate(90f)}
        //matrix.postRotate(90f)
        val bmp = Bitmap.createBitmap(ImageTon.bm, 0, 0,ImageTon.bm.width, ImageTon.bm.height, matrix, true)
        imageView2.setImageBitmap(bmp)
        //file 삭제
        ImageTon.img.delete()
        MediaScannerConnection.scanFile(
            context, arrayOf(ImageTon.img.absolutePath), null, null)
        //imageView2.setImageBitmap(ImageTon.img)


       /* // Populate the ViewPager and implement a cache of two media items
        val mediaViewPager = view.findViewById<ViewPager>(R.id.imageView2).apply {
            offscreenPageLimit = 2
            adapter = MediaPagerAdapter(childFragmentManager)
        }*/

        // Make sure that the cutout "safe area" avoids the screen notch if any
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Use extension method to pad "inside" view containing UI using display cutout's bounds
            view.findViewById<ConstraintLayout>(R.id.cutout_safe_area).padWithDisplayCutout()
        }

        // Handle back button press
        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            //fragmentManager?.popBackStack()
            activity?.finish()
        }

        // Handle share button press
        view.findViewById<ImageButton>(R.id.share_button).setOnClickListener {
            // Make sure that we have a file to share
            //갤러리에 저장
            //Toast.makeText(this,"로딩중이니 기다려주세요",Toast.LENGTH_LONG).show()
            share_button.isEnabled=false
            progressBar2.visibility=View.VISIBLE
             var outputDirectory = CameraActivity.getOutputDirectory(requireContext())


                val photoFile = File(
                    outputDirectory,
                    SimpleDateFormat(
                        "yyyy-MM-dd-HH-mm-ss-SSS",
                        Locale.US
                    ).format(System.currentTimeMillis()) + ".jpg"
                )
                val ostream: FileOutputStream = FileOutputStream(photoFile)
                bmp.compress(Bitmap.CompressFormat.JPEG, 75, ostream)
                PostImage(photoFile)
                ostream.close()

                /*MediaScannerConnection.scanFile(
                    context, arrayOf(photoFile.absolutePath), null, null)*/
                val mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(photoFile.extension)

                MediaScannerConnection.scanFile(
                    context, arrayOf(photoFile.absolutePath), arrayOf(mimeType), null
                )



            /*var intent=Intent(activity?.applicationContext, GoalDetailActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)*/

            /*Thread.sleep(1_500) //사진 보내질 동안 슬립
            activity?.finish()*/

            /*mediaList.getOrNull(mediaViewPager.currentItem)?.let { mediaFile ->  //추후 공유기능을 위해 주석처리


                // Create a sharing intent
                val intent = Intent().apply {
                    // Infer media type from file extension
                    val mediaType = MimeTypeMap.getSingleton()
                        .getMimeTypeFromExtension(mediaFile.extension)
                    // Get URI from our FileProvider implementation
                    val uri = FileProvider.getUriForFile(
                        view.context, BuildConfig.APPLICATION_ID + ".provider", mediaFile
                    )
                    val bm: Bitmap = BitmapFactory.decodeFile(mediaFile.extension)
                    // Set the appropriate intent extra, type, action and flags
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = mediaType
                    action = Intent.ACTION_SEND
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                // Launch the intent letting the user choose which app to share with
                startActivity(Intent.createChooser(intent, getString(R.string.share_hint)))
            }*/
        }


        //삭제예정
        // Handle delete button press
        /*view.findViewById<ImageButton>(R.id.delete_button).setOnClickListener {//삭제버튼
            AlertDialog.Builder(view.context, android.R.style.Theme_Material_Dialog)
                .setTitle(getString(R.string.delete_title))
                .setMessage("사진을 지우시겠습니까?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    mediaList[0].delete()
                    MediaScannerConnection.scanFile(
                        view.context, arrayOf(mediaList[0].absolutePath), null, null)
                    mediaList.removeAt(0)
                    activity?.finish()
                    }

                .setNegativeButton(android.R.string.no, null)
                .create().showImmersive()

        }*/
    }
    private fun ServerError(){
        Toast.makeText(activity?.applicationContext,"서버와의 연결이 종료되었습니다.초기화면으로 돌아갑니다",Toast.LENGTH_LONG).show()

        val intent=Intent(activity?.applicationContext,LoadingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityForResult(intent,2)
        activity?.finish()
    }
    private fun PostImage(file:File){
        //Retrofit 서버 연결
        //val file = File(thumbnail)
        val fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val part = MultipartBody.Part.createFormData("url", file.name, fileReqBody)
        val titleRequest=RequestBody.create(MediaType.parse("multipart/form-data"),TokenTon.postId.toString())

        val call= RetrofitGenerator.create().imagePost( titleRequest,part,"Token "+ TokenTon.Token,TokenTon.postId)

        call.enqueue(object : Callback<ImagePostResponse> {
            override fun onResponse(call: Call<ImagePostResponse>, response: Response<ImagePostResponse>) {
                //file.delete()
                //토큰 값 받아오기
                //Toast.makeText(this@AddGoalActivity,response.body()?.title.toString(),Toast.LENGTH_LONG).show()
                //TokenTon.set(response.body()?.token.toString())
                if(response.isSuccessful==false){
                    ServerError()}else{
                    activity?.finish()
                }
            }
            override fun onFailure(call: Call<ImagePostResponse>, t: Throwable) {
            }
        })
    }



}