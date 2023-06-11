package com.example.contactringtonenative

import android.content.*
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import java.io.*


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


        val fpath = "/storage/emulated/0/Download/exc_ogg.ogg"
        val file = File(fpath)

        val contact_number = "4445555"


        val fContUri = MediaStore.Audio.Media.getContentUriForPath(fpath)
        //        val fContUri = MediaStore.Audio.Media.getCollectionUriForFile()

        println("Deleting content uri ~ " + fContUri)
        resolver.delete(fContUri!!, null, null)

//        resolver.delete(fContUri!!,
//            MediaStore.MediaColumns.DATA + "=?", arrayOf( file.getAbsolutePath())
//            , null)


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

        val rt_uri = Uri.parse(ct_rt_uri)

        val rt_data: Cursor? = resolver.query(rt_uri, null, null, null, null)
        println("what is rt_data " + rt_data.toString())
        if (rt_data != null ) {
            if (rt_data!!.count > 0) {
                println("rtct ~" + rt_data!!.count.toString())
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
                        rtd_ii++
                        rt_data.moveToNext()
                    }
                }
                rt_data!!.close()
            }



            val projection = arrayOf(
                ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
            )

            val ct_data: Cursor? = resolver.query(
                lookupUri,
                projection, null, null, null
            )

            if (ct_data != null && ct_data.moveToFirst()) {

                ct_data.moveToFirst()
                // Get the contact lookup Uri
                val contactId = ct_data.getLong(0)
                val lookupKey = ct_data.getString(1)

                println("Uri String for Contact " + ContactsContract.Contacts.CONTENT_URI)

                println("Got Contact Data :: ID ~ " + contactId + " lookupKEY ~ " + lookupKey)


                println("setting file abs path ~ " + fpath)


//                val contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey)
                val contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, contactId.toString())
                println(" got contact lookup URI ~ " + contactUri.toString())
                println("update media file uri ~ " + fContUri.toString())

//                val mf_projection = arrayOf(
//                    MediaStore.MediaColumns._ID,
//                )
//
//                // get media string uri
//                val gmsuri = mediaUri.toString() + "/" + "exc_ogg.ogg"
//
//                val mf_data: Cursor? = resolver.query(
//                    mediaUri!! + "/",
//                    mf_projection, null, null, null
//                )
//                if (mf_data != null && mf_data.moveToFirst()) {
//
//                    mf_data.moveToFirst()
//                    val mft = mf_data.getType(0)
//                    println("got mftype ~ " + mft.toString())
//
//                    if (mft == 1) {
//                        val mid = ct_data.getLong(0)
//                        println("got mid ~ " + mid.toString())
//                    }
//                }


                val media_values: ContentValues = ContentValues()

                media_values.put(MediaStore.MediaColumns.DATA, fpath)
                media_values.put(MediaStore.MediaColumns.TITLE, "Exc 104")
                media_values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Exc DDD")
                media_values.put(MediaStore.MediaColumns.MIME_TYPE, getMIMEType(fpath))
////                media_values.put(MediaStore.MediaColumns.BITRATE, 160000)
////                media_values.put(MediaStore.MediaColumns._ID, 666)
////                media_values.put(MediaStore.MediaColumns.XMP, Blob())
////                media_values.put(MediaStore.MediaColumns.SIZE, 6666666)
                media_values.put(MediaStore.Audio.Media.IS_RINGTONE, 1)


//                collection =  MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY); // added in @api<=29 to get the primary external storage
//                values.put(MediaStore.Audio.Media.RELATIVE_PATH, (Environment.DIRECTORY_NOTIFICATIONS).getAbsolutePath());
//                Uri itemUri = resolver.insert(collection, values);


//                val updated = resolver.update(fContUri, media_values, null, null)
//                val updated = resolver.update(mediaUri!!, media_values, null, null)
//                val newUri: Uri? = resolver.insert(mediaUri!!, media_values)




                val contact_values: ContentValues = ContentValues()
//                contact_values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId.toString())
                contact_values.put(ContactsContract.Data.CUSTOM_RINGTONE, fpath)
                resolver.update(contactUri, contact_values, null, null);



            }

        ct_data!!.close()

        }


//    fun copyFile(sourceFile: File, destinationFile: File) {
//        try {
//            val inputStream = FileInputStream(sourceFile)
//            val outputStream = FileOutputStream(destinationFile)
//            val buffer = ByteArray(1024)
//            var length: Int
//
//            while (inputStream.read(buffer).also { length = it } > 0) {
//                outputStream.write(buffer, 0, length)
//            }
//
//            inputStream.close()
//            outputStream.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }



    fun getMIMEType(url: String?): String? {
        var mType: String? = null
        val mExtension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (mExtension != null) {
            mType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExtension)
        }
        return mType
    }


//    private fun SetAsRingtone(k: File, resolver: ContentResolver): Boolean {
//        val values = ContentValues()
//        values.put(MediaStore.MediaColumns.TITLE, k.name)
//        values.put(MediaStore.MediaColumns.MIME_TYPE, getMIMEType(k.getAbsolutePath()))
//        values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
//
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            val newUri: Uri? = resolver
//                .insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
//            try {
//                resolver.openOutputStream(newUri!!).use { os ->
//                    val size = k.length().toInt()
//                    val bytes = ByteArray(size)
//                    try {
//                        val buf = BufferedInputStream(FileInputStream(k))
//                        buf.read(bytes, 0, bytes.size)
//                        buf.close()
//                        os!!.write(bytes)
//                        os.close()
//                        os.flush()
//                    } catch (e: IOException) {
//                        return false
//                    }
//                }
//            } catch (ignored: Exception) {
//                return false
//            }
//
////            RingtoneManager.setActualDefaultRingtoneUri(
////                pass_context, type,
////                newUri
////            )
//            true
//        } else {
//            values.put(MediaStore.MediaColumns.DATA, k.absolutePath)
//            val uri = MediaStore.Audio.Media.getContentUriForPath(
//                k.absolutePath
//            )
//            resolver.delete(
//                uri!!,
//                MediaStore.MediaColumns.DATA + "=\"" + k.absolutePath + "\"",
//                null
//            )
//            val newUri: Uri? = resolver.insert(uri, values)
////            RingtoneManager.setActualDefaultRingtoneUri(
////                this@Emotes, type,
////                newUri
////            )
//            resolver
//                .insert(
//                    MediaStore.Audio.Media.getContentUriForPath(
//                        k.absolutePath
//                    )!!, values
//                )
//            true
//        }
//    }


}