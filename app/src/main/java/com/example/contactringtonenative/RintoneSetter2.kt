package com.example.contactringtonenative

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class RintoneSetter2 {


    fun setRingtoneByNumber(call_activity: AppCompatActivity) {

        val resolver: ContentResolver = call_activity.applicationContext.contentResolver
//        val file = File(
//            Environment.getExternalStorageDirectory().toString() +
//                    "/Test/ArjunMovieTelugu.mp3"
//        )
//        val file = File(
//            "/storage/emulated/0/Music/" +
//                    "Russian Circles - Mlàdek.mp3"
//        )

//        val fpath = "/storage/emulated/0/Download/exc_ogg.ogg"
        val fpath = "/storage/emulated/0/Download/exc_ogg.ogg"

        val contact_number = "4445555"
        val file = File(fpath)

        val fContUri = MediaStore.Audio.Media.getContentUriForPath(fpath)
        //        val fContUri = MediaStore.Audio.Media.getCollectionUriForFile()

        println("Deleting content uri ~ " + fContUri)
        //                resolver.delete(Uri.parse(file.absolutePath),null, null)



        val lookupUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            contact_number
        )

        var ct_rt_uri : String = "notfound"

        println("Ct uri ~ " + lookupUri.toString())
        // Query Contact Data
        val ct_data_test: Cursor? = resolver.query(lookupUri!!, null, null, null, null)

        while (ct_data_test!!.moveToNext()) {
            var col_idx = 0;
            for (col in ct_data_test.columnNames) {
                val columnKey: String = ct_data_test.getColumnName(col_idx)
                println("CT Column name ~ " + columnKey)
                val columnVal = ct_data_test.getString(col_idx)
                println("CT Col val ~ " + columnVal)
                if (columnKey == "custom_ringtone"){
                    ct_rt_uri = columnVal
                }
                col_idx += 1
            }
        }
        ct_data_test!!.close()

        println("check content vals for uri ~ " + ct_rt_uri)
//        pos content://media/external_primary/audio/media/1000000141
//        val rt_uri = Uri.parse("content://media/external_primary/audio/media/1000000142")
        val rt_uri = Uri.parse(ct_rt_uri)
        // Query ringtone data
        val rt_data: Cursor? = resolver.query(rt_uri, null, null, null, null)
        println("what is rt_data " + rt_data)
        println("rtct ~" + rt_data!!.count)
        if (rt_data!!.count > 0) {
            rt_data!!.moveToFirst()
            var rtd_ii = 0
            while (rtd_ii < rt_data.count) {
                println("rt data not null")
                var col_idx = 0;
                for (col in rt_data.columnNames) {

                    val columnKey: String = rt_data.getColumnName(col_idx)
                    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                    println("RT Column name ~ " + columnKey)
                    val rtt = rt_data.getType(col_idx)
                    println("get col type ~ " + rtt.toString())
                    if (rtt == 0) {
                        val rtv = "null"
                        println("rt val ~ " + rtv)
                    }
                    if (rtt == 1) {
                        val rtv = rt_data.getInt(col_idx)
                        println("rt val ~ " + rtv.toString())
                    }
                    if (rtt == 2) {
                        val rtv = rt_data.getFloat(col_idx)
                        println("rt val ~ " + rtv.toString())
                    }
                    if (rtt == 3) {
                        val rtv = rt_data.getString(col_idx)
                        println("rt val ~ " + rtv)
                    }
                    if (rtt == 4) {
                        val rtv = rt_data.getBlob(col_idx)
                        println("rt val ~ " + rtv.toString())
                    }
                    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                    col_idx += 1
                }
                rtd_ii ++
                rt_data.moveToNext()
            }

        }
            rt_data!!.close()



            val projection = arrayOf(
                ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
            )

            val ct_data: Cursor? = resolver.query(
                lookupUri,
                projection, null, null, null
            )

            resolver.delete(fContUri!!, null, null)

            if (ct_data != null && ct_data.moveToFirst()) {

                ct_data.moveToFirst()
                // Get the contact lookup Uri
                val contactId = ct_data.getLong(0)
                val lookupKey = ct_data.getString(1)

                println("Uri String for Contact " + ContactsContract.Contacts.CONTENT_URI)

                println("Got Contact Data :: ID ~ " + contactId + " lookupKEY ~ " + lookupKey)

                val contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey)

                println(" got contact lookup URI ~ " + contactUri.toString())
                println("setting file abs path ~ " + fpath)


//                val oldUri: Uri? =
//                    MediaStore.Audio.Media.getContentUriForPath(fpath);

                val localUri = Uri.withAppendedPath(Contacts.CONTENT_URI, contactId.toString())

                val media_values: ContentValues = ContentValues()


//                media_values.put(MediaStore.MediaColumns.DATA, fpath)
//                media_values.put(MediaStore.MediaColumns.TITLE, "Exc 104")
//                media_values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
//                media_values.put(MediaStore.Audio.Media.IS_RINGTONE, true)


//                val updated : Int = resolver.update(oldUri!!, media_values, null, null);
//                val newUri: Uri? = resolver.insert(oldUri!!, media_values)

                media_values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId.toString())
                media_values.put(ContactsContract.Data.CUSTOM_RINGTONE, fpath)
                resolver.update(localUri, media_values, null, null);
//                if (newUri != null) {
//                    val media_values: ContentValues = ContentValues()
//                media_values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, newUri.toString());
//                resolver.insert(contactUri, media_values);
//                    resolver.update(contactUri, media_values, null, null);
//                }


            } else {
                println("newuri is null")
            }

        ct_data!!.close()

        }
}