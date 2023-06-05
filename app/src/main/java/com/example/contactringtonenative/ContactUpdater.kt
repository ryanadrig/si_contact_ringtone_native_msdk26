package com.example.contactringtonenative

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.pathString


class ContactUpdater {

    private val selectedContactId = 0

    var REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    private fun checkAndRequestPermissions(call_activity: AppCompatActivity): Boolean {
        val readExternal =
            ContextCompat.checkSelfPermission(call_activity.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writeExternal =
            ContextCompat.checkSelfPermission(call_activity.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readContacts =
            ContextCompat.checkSelfPermission(call_activity.applicationContext, Manifest.permission.READ_CONTACTS)
        val writeContacts =
            ContextCompat.checkSelfPermission(call_activity.applicationContext, Manifest.permission.WRITE_CONTACTS)

        val readMedia =
            ContextCompat.checkSelfPermission(call_activity.applicationContext, Manifest.permission.READ_MEDIA_AUDIO)

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

        // Api level 31 and above which is pretty new
        if (readMedia != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_AUDIO)
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



    // build ringtone list
    var brt_list : ArrayList<Map<String, String>> = ArrayList()

    // ms_path music search path, rtt ring tone type : "default" or "audio"
    // Builds array of paths and type [{"path":"/expath", "type": "default"}, ...
    fun lookForMusicFiles(ms_path: String, rtt: String){
        println("looking in " + ms_path + " for music files")
        val fileDirMap =
            Files.list( Paths.get(ms_path) )
                .collect(Collectors.partitioningBy( { it -> Files.isDirectory(it)}))
        fileDirMap[false]?.forEach {
            println(it.fileName)
            var list_item : Map<String,String>
            list_item = mapOf("path" to ms_path + "/" + it.fileName, "type" to "audio")
            if (ms_path.contains("ringtone") || ms_path.contains("Ringtone")){
                list_item = mapOf("path" to ms_path + "/" + it.fileName, "type" to "default")
            }
            brt_list.add(list_item)

        }
        fileDirMap[true]?.forEach {
            println(it.fileName)
        }
    }

    fun getRealPathFromUri(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.getContentResolver()
                .query(contentUri!!, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
//            val real_path : String = cursor!!.getString(column_index)
        return "null"
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    fun query_audio_ms(call_activity: AppCompatActivity){
        println("get files from mediaquery")

        println("mediaquery ext cont uri ~ ")
        println(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)

//        val query_uri_str = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val query_uri_str = "content://storage/emulated/0/Music"
        val query_uri : Uri = Uri.parse(query_uri_str)
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE
//            MediaStore.Audio.Media.ALBUM
//                    MediaStore.Audio.Media._ID,
//            MediaStore.Audio.Media.DISPLAY_NAME
        )

        val selection = null //not filtering out any row.
        val selectionArgs = null //this can be null because selection is also null
        val sortOrder = null

        call_activity.applicationContext.contentResolver.query(
            query_uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            val titleColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
//            val albumColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)

            Log.d("MediaQuery Result ~ ", "Query found ${cursor.count} rows")

            while (cursor.moveToNext()) {
                val title = cursor.getString(titleColIndex)
//                val album = cursor.getString(albumColIndex)

//                Log.d("MediaQuery Result ~ ", "$title - $album")
                Log.d("MediaQuery Result ~ ", "$title ")
            }
        }
    }

// Default ringtones folder /system/media/audio/ringtones

    fun listMusic(call_activity: AppCompatActivity
                  ){
        println("Get files from default ringtones folder")
        val def_ringtones_path : String = "/system/media/audio/ringtones"



        checkAndRequestPermissions(call_activity)

        query_audio_ms(call_activity)


        lookForMusicFiles(def_ringtones_path, "default")



        println("Get files in external storage state dir ~ ")
        val ess_path_str = Environment.getExternalStorageDirectory().getPath()
        println(ess_path_str)

        val ess_path : Path = Paths.get(ess_path_str)

        if (Files.isDirectory(ess_path)){
            //List all items in the directory. Note that we are using Java 8 streaming API to group the entries by
            //directory and files
            val fileDirMap = Files.list(ess_path).collect(Collectors.partitioningBy( { it -> Files.isDirectory(it)}))

            println("Directories")
            //Print out all of the directories
            fileDirMap[true]?.forEach {
                        println(it.fileName)
                    if (it.fileName.pathString == "Music"){
                                println("found music folder")
                            lookForMusicFiles(ess_path_str + "/Music", "audio")
                    }
                if (it.fileName.pathString == "Ringtones"){
                    println("found ringtones folder")
                    lookForMusicFiles(ess_path_str + "/Ringtones", "default")
                }

                if (it.fileName.pathString == "Download"){
                    println("found downloads folder")
                    lookForMusicFiles(ess_path_str + "/Download", "audio")
                }
                      }

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

        println("final brt list ~ " )
        println(brt_list)

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