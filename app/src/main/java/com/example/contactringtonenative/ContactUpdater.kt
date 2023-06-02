package com.example.contactringtonenative

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.ContactsContract
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors


class ContactUpdater {

    private val selectedContactId = 0

    var REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    private fun checkAndRequestPermissions(call_context: Context,
                                            call_activity: AppCompatActivity): Boolean {
        val readExternal =
            ContextCompat.checkSelfPermission(call_context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writeExternal =
            ContextCompat.checkSelfPermission(call_context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readContacts =
            ContextCompat.checkSelfPermission(call_context, Manifest.permission.READ_CONTACTS)
        val writeContacts =
            ContextCompat.checkSelfPermission(call_context, Manifest.permission.WRITE_CONTACTS)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (readExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (writeExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (readContacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS)
        }
        if (writeContacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_CONTACTS)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                call_activity,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    fun listMusic(call_activity: AppCompatActivity){

        println("Get files in external storage state dir ~ ")
        println(Environment.getExternalStorageDirectory().getPath())
        val ess_path : Path = Paths.get(Environment.getExternalStorageDirectory().getPath())

        if (Files.isDirectory(ess_path)){
            //List all items in the directory. Note that we are using Java 8 streaming API to group the entries by
            //directory and files
            val fileDirMap = Files.list(ess_path).collect(Collectors.partitioningBy( { it -> Files.isDirectory(it)}))

            println("Directories")
            //Print out all of the directories
            fileDirMap[true]?.forEach { it -> println(it.fileName) }

            println("\nFiles")
            println("%-20s\tRead\tWrite\tExecute".format("Name"))
            //Print out all files and attributes
            fileDirMap[false]?.forEach( {it ->
                println("%-20s\t%-5b\t%-5b\t%b".format(
                    it.fileName,
                    Files.isReadable(it), //Read attribute
                    Files.isWritable(it), //Write attribute
                    Files.isExecutable(it))) //Execute attribute
            })
        } else {
            println("Enter a directory")
        }

//        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
//
//        val projection = arrayOf(
//            MediaStore.Audio.Media._ID,
//            MediaStore.Audio.Media.ARTIST,
//            MediaStore.Audio.Media.TITLE,
//            MediaStore.Audio.Media.DATA,
//            MediaStore.Audio.Media.DISPLAY_NAME,
//            MediaStore.Audio.Media.DURATION
//        )
//
//
//        val cursor : Cursor? = call_activity.applicationContext.getContentResolver().query(
//            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            projection,
//            selection,
//            null,
//            null
//        )
//
//        val songs: MutableList<String> = ArrayList()
//        while (cursor!!.moveToNext()) {
//            songs.add(
//                ((((cursor.getString(0) + "||" + cursor.getString(1)).toString() + "||" + cursor.getString(
//                    2
//                )).toString() + "||" + cursor.getString(3)).toString() + "||" + cursor.getString(4)).toString() + "||" + cursor.getString(
//                    5
//                )
//            )
//        }
    }

    fun updateContact(call_activity: AppCompatActivity){
        val values = ContentValues()

        val resolver: ContentResolver = call_activity.getContentResolver()




//        val file = File(Environment.getExternalStorageState() + "/Test/ArjunMovieTelugu.mp3")
//        if (file.exists()) {
//            val oldUri: Uri? = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath())
//            resolver.delete(
//                oldUri!!,
//                MediaStore.MediaColumns.DATA + "=\"" + file.getAbsolutePath() + "\"",
//                null
//            )
//            val contact_number = "CONTACT_NUMBER"
//            val lookupUri: Uri = Uri.withAppendedPath(
//                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
//                contact_number
//            )
//
//            // The columns used for `Contacts.getLookupUri`
//            val projection = arrayOf(
//                ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
//            )
//            val data: Cursor? =
//                call_activity.getContentResolver().query(lookupUri, projection, null, null, null)
//            if (data != null && data.moveToFirst()) {
//                data.moveToFirst()
//                // Get the contact lookup Uri
//                val contactId: Long = data.getLong(0)
//                val lookupKey: String = data.getString(1)
//                val contactUri: Uri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey)
//                values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath())
//                values.put(MediaStore.MediaColumns.TITLE, "Beautiful")
//                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
//                values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
//                val uri: Uri? = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath())
//                val newUri: Uri? = resolver.insert(uri!!, values)
//                if (newUri != null) {
//                    val uriString: String = newUri.toString()
//                    values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, uriString)
//                    Log.e("Uri String for " + ContactsContract.Contacts.CONTENT_URI, uriString)
//                    val updated = resolver.update(contactUri, values, null, null).toLong()
//                    Toast.makeText(call_activity.applicationContext, "Updated : $updated", Toast.LENGTH_LONG)
//                        .show()
//                }
//                data.close()
//            }
//        } else {
//            Toast.makeText(call_activity.applicationContext, "File does not exist", Toast.LENGTH_LONG).show()
//        }
    }


//    fun updateContact(dialog: DialogInterface?, which: Int, activity: AppCompatActivity) {
//        val raw: Cursor? = activity.getContentResolver().query(
//            ContactsContract.RawContacts.CONTENT_URI,
//            arrayOf<String>(ContactsContract.Contacts._ID),
//            ContactsContract.Data.CONTACT_ID + " = " + selectedContactId,
//            null,
//            null
//        )
//        if (!raw.moveToFirst()) {
//            return
//        }
//        val rawContactId: Int = raw.getInt(0)
//        val values = ContentValues()
//        when (which) {
//            DialogInterface.BUTTON_POSITIVE -> {
//                //User wants to add a new email
//                values.put(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID, rawContactId)
//                values.put(
//                    ContactsContract.Data.MIMETYPE,
//                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
//                )
//                values.put(ContactsContract.CommonDataKinds.Email.DATA, TEST_EMAIL)
//                values.put(
//                    ContactsContract.CommonDataKinds.Email.TYPE,
//                    ContactsContract.CommonDataKinds.Email.TYPE_OTHER
//                )
//                getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values)
//            }
//            else -> {
//                //User wants to edit selection
//                values.put(ContactsContract.CommonDataKinds.Email.DATA, TEST_EMAIL)
//                values.put(
//                    ContactsContract.CommonDataKinds.Email.TYPE,
//                    ContactsContract.CommonDataKinds.Email.TYPE_OTHER
//                )
//                getContentResolver().update(
//                    ContactsContract.Data.CONTENT_URI, values,
//                    ContactsContract.Data._ID + " = " + mEmail.getInt(0), null
//                )
//            }
//        }
//
//        //Don't need the email cursor anymore
//        mEmail.close()
//    }
}