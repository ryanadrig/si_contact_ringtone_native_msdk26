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




// Default ringtones folder /system/media/audio/ringtones

    fun listMusic(call_activity: AppCompatActivity
                  ){
        println("Get files from default ringtones folder")
        val def_ringtones_path : String = "/system/media/audio/ringtones"



        checkAndRequestPermissions(call_activity)

        RingtoneMediaUtil().query_audio_ms(call_activity)

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


    }

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