package com.example.contactringtonenative

import android.content.Context
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.pathString


class RingtoneMediaUtil {

    fun getMIMEType(url: String?): String? {
        var mType: String? = null
        val mExtension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (mExtension != null) {
            mType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExtension)
        }
        return mType
    }

    fun getUriFromDisplayName(context: Context, displayName: String): Uri? {
        println("Getting uri from display name")
        val extUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection: Array<String>
        projection = arrayOf(MediaStore.Files.FileColumns._ID)

        // TODO This will break if we have no matching item in the MediaStore.
        val cursor = context.contentResolver.query(
            extUri,
            projection,
            MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ?",
            arrayOf<String>(displayName),
            null
        )!!
        cursor.moveToFirst()
        return if (cursor.count > 0) {
            val columnIndex = cursor.getColumnIndex(projection[0])
            val fileId = cursor.getLong(columnIndex)
            cursor.close()
            println("Found file id " + fileId.toString())
            Uri.parse(extUri.toString() + "/" + fileId)
        } else {
            null
        }
    }

    // build ringtone list
    var brt_list: ArrayList<Map<String, String>> = ArrayList()

    // Default ringtones folder /system/media/audio/ringtones
    // ms_path music search path, rtt ring tone type : "default" or "audio"
    // Builds array of paths and type [{"path":"/expath", "type": "default"}, ...
    fun lookForMusicFiles(ms_path: String, rtt: String) : MutableList<Map<String, String>> {
        println("looking in " + ms_path + " for music files")
        if (!File(ms_path).exists()) {
            println("music file does not exist")
            return mutableListOf()
        }
        var ret_map : MutableList<Map<String, String>>
        = mutableListOf<Map<String,String>>()

        val fileDirMap =
            Files.list(Paths.get(ms_path))
                .collect(Collectors.partitioningBy({ it -> Files.isDirectory(it) }))

        var rmi : MutableMap<String, String> = mutableMapOf()
        fileDirMap[false]?.forEach {
            println(it.fileName)

            val fContUri = MediaStore.Audio.Media.getContentUriForPath(it.toString())
            println("lfmf attempt get file content uri ~ " + fContUri.toString())

            var list_item: Map<String, String>
            list_item = mapOf("path" to ms_path + "/" + it.fileName, "type" to "audio")
            if (ms_path.contains("ringtone") || ms_path.contains("Ringtone")) {
                list_item = mapOf("path" to ms_path + "/" + it.fileName, "type" to "default")
            }
            brt_list.add(list_item)
            ret_map.add(list_item)

        }
        fileDirMap[true]?.forEach {
            println(it.fileName)
        }
        return ret_map
    }

    fun listMusic(
        call_activity: AppCompatActivity
    ) {
        println("Get files from default ringtones folder")
        val def_ringtones_path: String = "/system/media/audio/ringtones"


        SICPermissionsUtil().checkAndRequestPermissions(call_activity)

        val qmedia: MutableList<MutableMap<String, String>> = query_audio_ms(call_activity)

        lookForMusicFiles(def_ringtones_path, "default")



        println("Get files in external storage state dir ~ ")
        val ess_path_str = Environment.getExternalStorageDirectory().getPath()
        println(ess_path_str)

        val ess_path: Path = Paths.get(ess_path_str)

        if (Files.isDirectory(ess_path)) {
            //List all items in the directory. Note that we are using Java 8 streaming API to group the entries by
            //directory and files
            val fileDirMap = Files.list(ess_path)
                .collect(Collectors.partitioningBy({ it -> Files.isDirectory(it) }))

            println("Directories")
            //Print out all of the directories
            fileDirMap[true]?.forEach {
                println(it.fileName)
                if (it.fileName.pathString == "Music") {
                    println("found music folder")
                    lookForMusicFiles(ess_path_str + "/Music", "audio")
                }
                if (it.fileName.pathString == "Ringtones") {
                    println("found ringtones folder")
                    lookForMusicFiles(ess_path_str + "/Ringtones", "default")
                }

                if (it.fileName.pathString == "Download") {
                    println("found downloads folder")
                    lookForMusicFiles(ess_path_str + "/Download", "audio")
                }
            }

            println("\nFiles")
            println("%-20s\tRead\tWrite\tExecute".format("Name"))

            //Print out all files and attributes
            fileDirMap[false]?.forEach({ it ->
                println(
                    "%-20s\t%-5b\t%-5b\t%b".format(
                        it.fileName,
                        Files.isReadable(it), //Read attribute
                        Files.isWritable(it), //Write attribute
                        Files.isExecutable(it)
                    )
                ) //Execute attribute
            })
        } else {
            println("Enter a directory")
        }

        println("final brt list ~ ")
        println(brt_list)


    }

    fun query_audio_ms(call_activity: AppCompatActivity): MutableList<MutableMap<String, String>> {

        println("get files from mediaquery")
        var qmedia: MutableList<MutableMap<String, String>> = mutableListOf()

//        println("mediaquery ext cont uri ~ ")
//        println(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        // "content://media/external/audio/media"
        var query_uri_str = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
//        val query_uri_str = "content://storage/emulated/0/Music"

        val query_uri: Uri = Uri.parse(query_uri_str)
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA
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

            val all_cols = cursor.columnNames
            for (elem in all_cols) {
                println("All media cols names ~ " + elem.toString())
            }

            val titleColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val dataColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
//            val albumColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)

            Log.d("MediaQuery Result ~ ", "Query found ${cursor.count} rows")

            while (cursor.moveToNext()) {
                val title = cursor.getString(titleColIndex)
                val mr_data = cursor.getString(dataColIndex)
//                val album = cursor.getString(albumColIndex)

//                Log.d("MediaQuery Result ~ ", "$title - $album")
                Log.d("MediaQuery Result ~ ", "$title " + " data ~ " + mr_data)

                val qmitem: MutableMap<String, String> =
                    mutableMapOf(
                        "qmtitle" to title,
                        "path" to mr_data
                    )
                qmedia.add(qmitem)
            }

            cursor.close()
        }

return qmedia
    }


    fun getAllMusicGeneric(call_activity: AppCompatActivity){
        println("Get all music generic called")

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
        )

        val cursor: Cursor? = call_activity.applicationContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        val songs: MutableList<String> = ArrayList()
        while (cursor!!.moveToNext()) {
            songs.add(
                ((((cursor.getString(0) + "||" + cursor.getString(1)).toString() + "||" + cursor.getString(
                    2
                )).toString() + "||" + cursor.getString(3)).toString() + "||" + cursor.getString(4)).toString() + "||" + cursor.getString(
                    5
                )
            )
        }
        println("all generic songs ~~ " + songs.toString())
    }


    fun getAllMusicRemDups(call_activity: AppCompatActivity){
        println("Get all music remove duplicates called")

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
//            MediaStore.Audio.Media.DURATION
        )

        val cursor: Cursor? = call_activity.applicationContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        val songs: MutableList< MutableMap<String, String>> = ArrayList()
        var songno = 0
        while (cursor!!.moveToNext()) {
            var song_data:MutableMap<String, String>

            var song_exists : Boolean = false
            for(sn in songs){
                if (sn["title"] == cursor.getString(2)){
                    song_exists = true
                }
            }

            if (song_exists == false) {
                song_data = mutableMapOf(
                    "song_id" to cursor.getString(0),
                    "artist" to cursor.getString(1),
                    "title" to cursor.getString(2),
                    "data" to cursor.getString(3),
                    "display_name" to cursor.getString(4),
                )
                songs.add(song_data)
            }
        }
        println("all generic songs ~~ " + songs.toString())
    }


    fun getAllRingtones(call_activity: AppCompatActivity): MutableList<MutableMap<String, String>>{
        val manager = RingtoneManager(call_activity)
        manager.setType(RingtoneManager.TYPE_RINGTONE)
        val cursor = manager.cursor

//        val list: MutableMap<String, String> = HashMap()
        val build_ret_list : MutableList<MutableMap<String, String>> = mutableListOf()
        cursor.moveToFirst()
        val notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
        println("rt not title ~ " + notificationTitle)
        val notificationUri =
            cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(
                RingtoneManager.ID_COLUMN_INDEX
            )
        val song0 = mutableMapOf("title" to notificationTitle,
            "contentURI" to notificationUri)
        build_ret_list.add(song0)
        while (cursor.moveToNext()) {
            val notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            println("rt not title ~ " + notificationTitle)
            val notificationUri =
                cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(
                    RingtoneManager.ID_COLUMN_INDEX
                )
            println("rt not uri ~ " + notificationUri)
            val songadd = mutableMapOf("title" to notificationTitle,
                "contentURI" to notificationUri,
                "media_type" to "ringtone"
            )

            build_ret_list.add(songadd)
        }
        cursor.close()
        println("getAllRingtones build ret list ~ " + build_ret_list.toString())
        return build_ret_list
    }
    fun getAllMusicAndRTRemDups(call_activity: AppCompatActivity): String{
        println("Get all music remove duplicates called")

        val gar_list: MutableList<MutableMap<String, String>> = getAllRingtones(call_activity)

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
//            MediaStore.Audio.Media.DURATION
        )

        val cursor: Cursor? = call_activity.applicationContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

//        val songs: MutableList< MutableMap<String, String>> = ArrayList()
        var songno = 0
        var songs = gar_list
        while (cursor!!.moveToNext()) {
            var song_data:MutableMap<String, String>

            var song_exists : Boolean = false
            for(sn in songs){
                if (sn["title"] == cursor.getString(2)){
                    song_exists = true
                }
            }

            if (song_exists == false) {
                song_data = mutableMapOf(
                    "song_id" to cursor.getString(0),
                    "artist" to cursor.getString(1),
                    "title" to cursor.getString(2),
                    "data" to cursor.getString(3),
                    "display_name" to cursor.getString(4),
                    "media_type" to "music"
                )
                songs.add(song_data)
            }
        }
        cursor.close()
        println("all ringtones and songs rem dups~~ " + songs.toString())
        val gson = Gson()
        val jsonres = gson.toJson(songs)
        val jsonresstr = jsonres.toString()
        println("all ringtones and songs rem dups json conv str ~~ " + jsonresstr)
        return jsonresstr
    }


}