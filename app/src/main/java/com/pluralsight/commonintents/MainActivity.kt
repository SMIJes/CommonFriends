package com.pluralsight.commonintents

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.AlarmClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.gms.actions.NoteIntents
import kotlinx.android.synthetic.main.layout_message_input.*
import kotlinx.android.synthetic.main.layout_user_feed.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_IMAGE_CAPTURE = 1
private const val REQUEST_IMAGE_GET = 2

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonReminder.setOnClickListener {
            onButtonReminderClick()
        }

        buttonAttach.setOnClickListener {
            onButtonAttachClick()
        }

        buttonPick.setOnClickListener {
            onButtonPickClick()
        }

        imageViewAvatar.setOnClickListener {
            openProfile()
        }

        textViewName.setOnClickListener {
            openProfile()
        }

        buttonSave.setOnClickListener {
            saveMessageAsNote()
        }
    }

    private fun saveMessageAsNote() {
        val intent = Intent(NoteIntents.ACTION_CREATE_NOTE).apply {
            putExtra(NoteIntents.EXTRA_NAME, "Message Subject")
            putExtra(NoteIntents.EXTRA_TEXT, "Message Text")
            type = "text/plain"
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }else{
            todo("No Activity found for this Intent")
        }
    }

    private fun openProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun onButtonAttachClick() {
        uriSavedImage = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".provider",
            createImageFile()
        )
        Log.i("MainActivity", "Storing image in $uriSavedImage")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage)
        }
        if(intent.resolveActivity(packageManager)!=null){
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private var uriSavedImage: Uri? = null
    private var currentPhotoPath : String = ""

    //Generates contentPath(File) and uri of the to-be-captured image
    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        //create the image filename
        val timeStamp:String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            //Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath }
    }

    //Picks an image file using the ACTION_GET_CONTENT, and filter for images only
    private fun onButtonPickClick() {
        //ACTION_GET_CONTENT works with any type of file, so the mimeType needs to be specified
        //Allows selection of multiple files

        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"

            //for multiple pictures selection
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        if(intent.resolveActivity(packageManager)!=null){
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //For captured image (Attach Button)
        if(requestCode== REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK){
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriSavedImage)
            imageViewAttachmentPreview.visibility = View.VISIBLE
            imageViewAttachmentPreview.setImageBitmap(bitmap)

         //for selected image (Pick button)
        }else if (requestCode== REQUEST_IMAGE_GET && resultCode== RESULT_OK){
            //for single picture selection
            //val fullPhotoUri: Uri = data?.data ?: error("Missing data")

            //For multiple selection
            val clipData = data?.clipData ?: error("Missing ClipData")
            val itemCount = clipData.itemCount
            Log.d("MainActivity", "Item Count: $itemCount")
            val item = clipData.getItemAt(0)
            val fullPhotoUri = item.uri

            //For display of selected picture
            Log.d("MainActivity", "Foto URI: $fullPhotoUri")
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fullPhotoUri)
            imageViewPickPreview.visibility =View.VISIBLE
            imageViewPickPreview.setImageBitmap(bitmap)

        }
    }

    private fun onButtonReminderClick() {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "Write a post")
            putExtra(AlarmClock.EXTRA_HOUR, 17)
            putExtra(AlarmClock.EXTRA_MINUTES, 0)
            putExtra(AlarmClock.EXTRA_DAYS, arrayListOf(
                Calendar.MONDAY,
                Calendar.TUESDAY,
                Calendar.WEDNESDAY,
                Calendar.THURSDAY,
                Calendar.FRIDAY,
            ))
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}
