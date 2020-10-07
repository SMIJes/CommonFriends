package com.pluralsight.commonintents

import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.actions.ReserveIntents
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*

private const val MY_PERMISSION_REQUEST_CALL:Int = 2

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        buttonCalendar.setOnClickListener {
            onClickCalendar()
        }

        buttonMaps.setOnClickListener {
            onClickMaps()
        }

        buttonTaxi.setOnClickListener {
            onClickTaxi()
        }

        buttonSearch.setOnClickListener {
            omClickSearch()
        }

        buttonListen.setOnClickListener {
            onClickListen()
        }

        buttonWebsite.setOnClickListener {
            onClickWeb()
        }

        buttonCall.setOnClickListener {
            onClickCall()
        }

        buttonEmail.setOnClickListener {
            onClickEmail()
        }

        buttonContact.setOnClickListener {
            onClickContact()
        }
    }

    private fun onClickContact()  {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME, "Anne Droid")
            putExtra(ContactsContract.Intents.Insert.EMAIL, "anne@example.com")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    //Can be used for SMS too, with lil changes
    private fun onClickEmail() {val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // only email apps should handle this //for email
        //data = Uri.parse("smsto:08011111111") // for SMS
        putExtra(Intent.EXTRA_EMAIL, arrayOf(
            "anne@example.com",
            "jzee@example.com",
            "kanye@west.com"
        ))
        putExtra(Intent.EXTRA_SUBJECT, "Testing Email")
        putExtra(Intent.EXTRA_TEXT, "Good morning... ") //for email body
        //putExtra("sms_body", "Good morning... ") //for sms body
    }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    //Can be performed by either ACTION_DIAL(starts the dialer app but lets the user starts the call)
    // or ACTION_CALL(starts the call directly) which requires asking for permission at runtime
    private fun onClickCall() {
        //By ACTION_CALL
//        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
//        != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.CALL_PHONE),
//                MY_PERMISSION_REQUEST_CALL
//            )
//        }else{
//            performCall()
//        }

        //By ACTION_DIAL
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:(123) 456 890")
        }
        if(intent.resolveActivity(packageManager)!=null)
            startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            MY_PERMISSION_REQUEST_CALL -> {
                if(grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    performCall()
                }
            }
        }
    }

    private fun performCall(){
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:(123) 456 890")
        }
        if(intent.resolveActivity(packageManager)!=null)
            startActivity(intent)
    }

    private fun onClickWeb()  {
        val webPage: Uri = Uri.parse("http://www.example.com")
        val intent = Intent(Intent.ACTION_VIEW, webPage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }else{
            todo("It appears as if you don't have a Web Browser")
        }
    }

    private fun onClickListen() {
        val artist = "Phyno"
        val song = "Oso Ga Eme"

        val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
            /*Indicates that we wanna search for an artist*/
            //putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE)
            /*Indicates that we wanna search for a song*/
            putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/audio")
            /*Indicates the artist we wanna search for*/
            putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist)
            /*Indicates the song we wanna search for*/
            putExtra(MediaStore.EXTRA_MEDIA_TITLE, song)
            /*Added for compatibility purpose*/
            putExtra(SearchManager.QUERY, "$artist $song")
        }
        if(intent.resolveActivity(packageManager)!=null){
            startActivity(intent)
        }
    }

    private fun omClickSearch() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, "gardening")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun onClickTaxi() {
        //Don't bother yourself, no app will handle this intent
        val intent = Intent(ReserveIntents.ACTION_RESERVE_TAXI_RESERVATION)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun onClickMaps() {
        val address = "182 N. Union Ave, Farmington, UT 84025"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("geo:0,0?q=$address")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }else{
            todo("It appears as if you don't have a map app")
        }
    }

    private fun onClickCalendar()  {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, Calendar.MARCH)
        calendar.set(Calendar.DAY_OF_MONTH, 21)
        val dateLong = calendar.timeInMillis

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "Anne Droid Birthday")
            //putExtra(CalendarContract.Events.EVENT_LOCATION, location) //not mandatory
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateLong)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dateLong)
            //This causes the calendar reminder event to repeat yearly
            putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY")
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    /*Just to show how this is done, there is no implementation here*/
    private fun openAnyKindOfFileFromWeb(){
        val dataUri = Uri.parse("the web link")

        val intent =  Intent(Intent.ACTION_VIEW).apply {
            type = "audio/mpeg3"
            data = dataUri
        }
        if (intent.resolveActivity(packageManager)!=null){
            startActivity(intent)
        }
    }
}
