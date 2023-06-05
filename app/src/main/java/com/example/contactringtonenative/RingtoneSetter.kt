package com.example.contactringtonenative

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class RingtoneSetter {

    fun setRingtoneByNumber(call_activity: AppCompatActivity) {
        val values = ContentValues()
        val resolver: ContentResolver = call_activity.applicationContext.contentResolver
        val file = File(
            Environment.getExternalStorageDirectory().toString() + "/Test/ArjunMovieTelugu.mp3"
        )
        if (file.exists()) {
            val oldUri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
            resolver.delete(
                oldUri!!,
                MediaStore.MediaColumns.DATA + "=\"" + file.absolutePath + "\"",
                null
            )
            val contact_number = "CONTACT_NUMBER"
            val lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                contact_number
            )

            // The columns used for `Contacts.getLookupUri`
            val projection = arrayOf(
                ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
            )
            val data: Cursor? = resolver.query(lookupUri, projection, null, null, null)
            if (data != null && data.moveToFirst()) {
                data.moveToFirst()
                // Get the contact lookup Uri
                val contactId = data.getLong(0)
                val lookupKey = data.getString(1)
                val contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey)
                values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
                values.put(MediaStore.MediaColumns.TITLE, "Beautiful")
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
                values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
                val uri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
                val newUri = resolver.insert(uri!!, values)
                if (newUri != null) {
                    val uriString = newUri.toString()
                    values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, uriString)
                    Log.e("Uri String for " + ContactsContract.Contacts.CONTENT_URI, uriString)
                    val updated = resolver.update(contactUri, values, null, null).toLong()
//                    Toast.makeText(this@RingtoneChange, "Updated : $updated", Toast.LENGTH_LONG).show()
                }
                data.close()
            }
        } else {
//            Toast.makeText(this@RingtoneChange, "File does not exist", Toast.LENGTH_LONG).show()
        }
    }
}