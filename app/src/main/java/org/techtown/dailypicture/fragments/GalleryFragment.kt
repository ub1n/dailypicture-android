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
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_gallery.*
/*import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc*/
import org.techtown.dailypicture.R
import org.techtown.dailypicture.utils.showImmersive
import org.techtown.dailypicture.utils.padWithDisplayCutout
import org.techtown.dailypicture.BuildConfig
import org.techtown.dailypicture.CameraActivity
import org.techtown.dailypicture.GoalDetailActivity
import org.techtown.dailypicture.testRoom.Picture
import org.techtown.dailypicture.testRoom.PictureDao
import org.techtown.dailypicture.testRoom.PictureDatabase
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer

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
    /*fun imageToBitmap(image:Image): Bitmap {
        val mYuvMat=imageToMat(image, CvType.CV_8UC1)
        val bgrMat= Mat(image.width,image.height,CvType.CV_8UC4)
        Imgproc.cvtColor(mYuvMat,bgrMat,Imgproc.COLOR_YUV2BGR_I420)
        val rgbaMatOut=Mat()
        Imgproc.cvtColor(bgrMat,rgbaMatOut,Imgproc.COLOR_RGB2RGBA,0)

        var bitmap=Bitmap.createBitmap(bgrMat.cols(),bgrMat.rows(),Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(rgbaMatOut,bitmap)
        val matrix= Matrix()
        matrix.postRotate(90f)
        bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
        return bitmap
    }
    fun imageToMat(image:Image,type:Int):Mat{
        var buffer:ByteBuffer
        var rowStride:Int
        var pixelStride:Int
        val width=image.width
        val height=image.height
        var offset=0

        val planes=image.planes
        val data=ByteArray(image.width*image.height*ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)/8)
        val rowData=ByteArray(planes[0].rowStride)

        for(i in planes.indices){
            buffer=planes[i].buffer
            buffer.rewind()
            rowStride=planes[i].rowStride
            pixelStride=planes[i].pixelStride
            val w=if(i==0) width else width/2
            val h=if(i==0) height else height/2
            for (row in 0 until h){
                val bytesPerPixel=ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)/8
                if(pixelStride==bytesPerPixel){
                    val length=w*bytesPerPixel
                    buffer.get(data,offset,length)

                    if(h-row !=1){
                        buffer.position(buffer.position()+rowStride-length)
                    }
                    offset+=length
                }else{
                    if(h-row==1){
                        buffer.get(rowData,0,width-pixelStride+1)
                    }else{
                        buffer.get(rowData,0,rowStride)
                    }
                    for(col in 0 until w){
                        data[offset++]=rowData[col*pixelStride]
                    }
                }
            }

        }
        val mat=Mat(height+height/2,width,type)
        mat.put(0,0,data)
        return mat
    }*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var bm: Bitmap = BitmapFactory.decodeFile(mediaList[0].path)
        val matrix = Matrix()
        matrix.postRotate(90f)
        val bmp = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        imageView2.setImageBitmap(bmp)


        //  imageView2.setImageBitmap(bmp)
        //val bmp:Bitmap= Images.Media.Bitmap(activity?.contentResolver,uri)
        /* val previewImageReader:ImageReader=ImageReader.newInstance(800,800, ImageFormat.YUV_420_888,1)
        val previewCallback:ImageReader.OnImageAvailableListener=ImageReader.OnImageAvailableListener {
            @Override
            fun onImageAvailable(reader:ImageReader){
                var image:Image=reader.acquireLatestImage()
                var planes:Array<Image.Plane>
                planes=image.getPlanes()
                var buffer: ByteBuffer=planes[0].buffer
                var data:ByteArray=ByteArray(buffer.capacity())
                buffer.get(data)
                val bitmap:Bitmap=BitmapFactory.decodeByteArray(data,0,data.size,null)
                imageView2.setImageBitmap(bitmap)

                image.close()
            }
        }
        var thread:HandlerThread= HandlerThread("CameraPciture")
        thread.start()
        val handler: Handler =Handler(thread.looper)
        previewImageReader.setOnImageAvailableListener(previewCallback,handler)
        previewCallback.onImageAvailable(previewImageReader)

        // Populate the ViewPager and implement a cache of two media items
        val mediaViewPager = view.findViewById<ViewPager>(R.id.imageView2).apply {
            offscreenPageLimit = 2
            adapter = MediaPagerAdapter(childFragmentManager)
        }

        // Make sure that the cutout "safe area" avoids the screen notch if any
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Use extension method to pad "inside" view containing UI using display cutout's bounds
            view.findViewById<ConstraintLayout>(R.id.cutout_safe_area).padWithDisplayCutout()
        }
*/
        // Handle back button press
        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            //fragmentManager?.popBackStack()
            activity?.finish()
        }

        // Handle share button press
        view.findViewById<ImageButton>(R.id.share_button).setOnClickListener {
            // Make sure that we have a file to share
            var picture=Picture()
            var stream = ByteArrayOutputStream()
            bmp?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            var byteArray = stream.toByteArray()

            var database: PictureDatabase = PictureDatabase.getInstance(activity!!.applicationContext)
            var pictureDao: PictureDao =database.pictureDao
            picture.image=byteArray
            Thread { pictureDao.insert(picture) }.start()
            //
            //activity?.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            //activity?.setResult(Activity.RESULT_OK,intent)
            activity?.finish()

            /*mediaList.getOrNull(mediaViewPager.currentItem)?.let { mediaFile ->


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



        // Handle delete button press
        view.findViewById<ImageButton>(R.id.delete_button).setOnClickListener {
            AlertDialog.Builder(view.context, android.R.style.Theme_Material_Dialog)
                .setTitle(getString(R.string.delete_title))
                .setMessage(getString(R.string.delete_dialog))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    mediaList[0].delete()
                    MediaScannerConnection.scanFile(
                        view.context, arrayOf(mediaList[0].absolutePath), null, null)
                    mediaList.removeAt(0)
                    activity?.finish()
                    /*mediaList.getOrNull(mediaViewPager.currentItem)?.let { mediaFile ->

                        // Delete current photo
                        mediaFile.delete()

                        // Send relevant broadcast to notify other apps of deletion
                        MediaScannerConnection.scanFile(
                                view.context, arrayOf(mediaFile.absolutePath), null, null)

                        // Notify our view pager
                        mediaList.removeAt(mediaViewPager.currentItem)
                        mediaViewPager.adapter?.notifyDataSetChanged()

                        // If all photos have been deleted, return to camera
                        if (mediaList.isEmpty()) {
                            fragmentManager?.popBackStack()
                        }
                    }*/}

                .setNegativeButton(android.R.string.no, null)
                .create().showImmersive()

        }
    }

}