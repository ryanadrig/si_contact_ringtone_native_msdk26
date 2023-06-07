package com.example.contactringtonenative

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class RingtoneMediaUtil {

    fun query_audio_ms(call_activity: AppCompatActivity){
        println("get files from mediaquery")

        println("mediaquery ext cont uri ~ ")
        println(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)

        val query_uri_str = "content://media/external/audio/media"
//        val query_uri_str = "content://storage/emulated/0/Music"
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
    
}