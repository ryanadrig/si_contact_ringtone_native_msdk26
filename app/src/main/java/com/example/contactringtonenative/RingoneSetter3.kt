package com.example.contactringtonenative

import android.app.Activity
import android.content.ContentValues
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.SyncStateContract.Helpers.insert
import java.io.File
import java.security.AccessController.getContext

class RingoneSetter3 {

    fun setRingtone(call_activity: Activity){

        println("Set List ringtone called")
        val ringFile: File = File("/storage/emulated/0/Download/exc_ogg.ogg")
        val rfp = ringFile.absolutePath
        println("ring file abs path ~ " + rfp)
        var values: ContentValues =  ContentValues();
        values.put(MediaStore.MediaColumns.DATA, rfp);
        values.put(MediaStore.MediaColumns.TITLE, "exc ring Lset");

        val getMimeType = RingtoneMediaUtil().getMIMEType(ringFile.absolutePath)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.MediaColumns.SIZE, ringFile.length());
//        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        val uri: Uri? = MediaStore.Audio.Media.getContentUriForPath(ringFile.getAbsolutePath());
        val newUri: Uri? = call_activity.contentResolver.insert(uri!!, values);


        try {
//            RingtoneManager.setActualDefaultRingtoneUri(call_activity.applicationContext,
//                RingtoneManager.TYPE_RINGTONE, newUri);
            val rc: Cursor = RingtoneManager(call_activity).getCursor()

        } catch (t: Throwable) {
            println("error in set list ringtone ~ " + t.message)
        }
    }

}