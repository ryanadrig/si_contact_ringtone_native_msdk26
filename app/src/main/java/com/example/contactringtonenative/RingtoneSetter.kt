package com.example.contactringtonenative

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.annotations.Contract
import java.io.File


class RingtoneSetter {

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

        val contact_number = "4445555"
        val file = File("/storage/emulated/0/Download/exc_ogg.ogg")

        val fContUri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
//        val fContUri = MediaStore.Audio.Media.getCollectionUriForFile()

        println("Deleting content uri ~ " + fContUri)
//                resolver.delete(Uri.parse(file.absolutePath),null, null)
        resolver.delete(fContUri!!,null, null)
        val projection2 = arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
        val cursor2: Cursor? = call_activity.contentResolver.
        query(fContUri!!, projection2, null, null, null)

        if (cursor2 != null) {
            try {
                // Iterate through the cursor to get the file details
                while (cursor2.moveToNext()) {

                    val gdn = cursor2.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val gs =   cursor2.getColumnIndex(OpenableColumns.SIZE)
                    if (gdn >= 0 &&
                        gs >= 0
                    ) {
                        val displayName =
                            cursor2.getString(gdn)
                        val size =
                            cursor2.getLong(gs)

                        println("CC2 File Name: $displayName")
                        println("CC2 File Size: $size bytes")
                    }
                    else{
                        println("column for disp name or size not existing")
                    }
                }

            } finally {
                // Close the cursor when finished
                cursor2.close()
            }
        }



        if (file.exists()) {

            val fileMediaExtUriTEST =
                RingtoneMediaUtil().getUriFromDisplayName(
                    call_activity,
                    file.name)

            println("Get file type ~ " + resolver.getType(fileMediaExtUriTEST!!).toString())
            println("TEST   Deleting original content uri for song ~ " + fileMediaExtUriTEST.toString())

            println("reversing media url to check path")
            val cursor: Cursor? = resolver
                .query(fileMediaExtUriTEST!!,
                    arrayOf<String>(
                        MediaStore.MediaColumns.TITLE,
                        MediaStore.MediaColumns.DATA), null, null, null)
//            cursor!!.moveToFirst()
            while (cursor!!.moveToNext()) {
                println("mediastore query loop")
                val title = cursor.getString(0)
                val filePath = cursor.getString(1)
                println("title ~ " + title +" path ~ "  + filePath)

                //Fatal
//                            resolver.delete(oldUriTEST, null, null)
            }
            cursor.close()

            try {

                // no access plus reverse content from song name giving different location if it's in two places
//                resolver.delete(
//                    oldUriTEST!!,
//                    MediaStore.MediaColumns.DATA + "='" + file.absolutePath + "'",
//                    null
//                )



                println("start all column lookup")


                val lookupUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    contact_number
                )

                // The columns used for `Contacts.getLookupUri`
                val projection = arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.CUSTOM_RINGTONE
                )
                println("Start all contact resolver query")
                val data: Cursor? = resolver.query( lookupUri,
                    projection
                    , null, null, null, null)

                println("check all contact proj data")
                if (data != null){
                    println("loop contact datas")
                    var col_idx = 0
                    data.moveToFirst()
                    for (di in data.columnNames){
                        println("data col name ~ " + di)

                        println("Data col val ~ " + data.getString(col_idx))
                        col_idx += 1
                    }
                }
                data!!.close()
            }
            catch(e: Exception){
                println("exception deleting test ~ " + e.printStackTrace())
            }


            val lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                contact_number
            )

            // The columns used for `Contacts.getLookupUri`
            val projection = arrayOf(
                Contacts._ID, Contacts.LOOKUP_KEY
            )
            val data: Cursor? = resolver.query(lookupUri, projection, null, null, null)
            val values = ContentValues()

            if (data != null && data.moveToFirst()) {

                try {
                    data.moveToFirst()
                    // Get the contact lookup Uri
                    val contactId = data.getLong(0)
                    val lookupKey = data.getString(1)

                    println("Uri String for Contact " + Contacts.CONTENT_URI )

                    println("Got Contact Data :: ID ~ " + contactId + " lookupKEY ~ " + lookupKey )

                    val contactUri = Contacts.getLookupUri(contactId, lookupKey)

                    println(" got contact lookup URI ~ " + contactUri.toString())
                    println("setting file abs path ~ " + file.absolutePath)


//                    values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
//                    values.put(MediaStore.MediaColumns.TITLE, "Heyoka Suavage Ss")
//                    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
//                    values.put(MediaStore.Audio.Media.IS_RINGTONE, true)


//                    values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
//                    values.put(
//                        MediaStore.MediaColumns.TITLE, "Heyoka Suavage 88")


//                    values.put(
//                        ContactsContract.Contacts.CUSTOM_RINGTONE, fileMediaExtUriTEST.toString()
//                    )

//                    values.put(
//                        ContactsContract.Contacts.CUSTOM_RINGTONE, fContUri.toString()
//                    )
//                    values.put(MediaStore.Audio.Media.IS_RINGTONE, true)
//                    values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
//
//                    println("Contacts contract cont item type ~ " + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                    println("Cont cont mimetype " + ContactsContract.Data.MIMETYPE)
//                    println("cont cont rintone common " + ContactsContract.CommonDataKinds.Phone.CUSTOM_RINGTONE)

                    val set_file_path = fContUri.toString() + "/" +file.name
                    println("Setting with path ~ " +  set_file_path)
                    println("Setting with contact uri ~ "+ contactUri)

//                    values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId.toString())
//                    values.put(ContactsContract.Data.CUSTOM_RINGTONE, set_file_path)

                    values.put(MediaStore.MediaColumns.DATA, fContUri.toString())
                    values.put(MediaStore.MediaColumns.TITLE, "EXCis 90")
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg")
                    values.put(MediaStore.Audio.Media.IS_RINGTONE, true)

//                    val uriString = newUri.toString()
//                       val updated = values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, uriString)

                    val newUri = resolver.insert(fContUri!!, values)

                    val uriString = newUri.toString()

                    val updated = values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, uriString)

//                    resolver.update(
//                        contactUri,
//                        values,
//                     null,
//                        null
//                    )

                    println("values put into ContentValues complete")


                    // from stackoverflow
////                    val newUri = resolver.insert(fContUri!!, values)
////                    val newUri= resolver.update(uri!!, values, null, null)
//
//                    if (newUri != null) {
//                        val uriString = newUri.toString()
////                       val updated = values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, uriString)
////                        val updated = resolver.update(contactUri, values, null, null).toLong()
//                        println("Uri String for Media " + uriString)
//                        println("Updated ret ~ " + updated.toString())
//
//                    }


                    // from chatgpt



                }catch (e: Exception){
                    println("Exception setting ringtone ~ " + e.toString())
                }

                data.close()
            }
            else{
                println("newuri is null")
            }
        } else {
            println("file is not existing")
        }
    }



}