package com.example.contactringtonenative

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SICPermissionsUtil {

    var REQUEST_ID_MULTIPLE_PERMISSIONS = 1

    fun checkAndRequestPermissions(call_activity: AppCompatActivity): Boolean {
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

}