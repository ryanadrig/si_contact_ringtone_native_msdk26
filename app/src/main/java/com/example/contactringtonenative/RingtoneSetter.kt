package com.example.contactringtonenative

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import java.io.File


fun getUriFromDisplayName(context: Context, displayName: String): Uri? {
    println("Getting uri from display name")
    val extUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection: Array<String>
    projection = arrayOf(MediaStore.Files.FileColumns._ID)

    // TODO This will break if we have no matching item in the MediaStore.
    val cursor = context.contentResolver.query(
        extUri, projection,
        MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE ?", arrayOf<String>(displayName), null
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
class RingtoneSetter {

    fun setRingtoneByNumber(call_activity: AppCompatActivity) {
        val values = ContentValues()
        val resolver: ContentResolver = call_activity.applicationContext.contentResolver
//        val file = File(
//            Environment.getExternalStorageDirectory().toString() +
//                    "/Test/ArjunMovieTelugu.mp3"
//        )
        val file = File(
            "/storage/emulated/0/Music/" +
                    "Russian Circles - Mlàdek.mp3"
        )


        if (file.exists()) {

            val TESTOldFile =
                    "/storage/emulated/0/Music/Russian Circles - Mlàdek.mp3"

//            val oldUriTEST = MediaStore.Audio.Media.getContentUriForPath(
//                "/storage/emulated/0/Music/Russian Circles - Mlàdek.mp3")

            val oldUriTEST = getUriFromDisplayName(
                call_activity,
                "Russian Circles - Mlàdek.mp3")


            println("TEST   Deleting original content uri for song ~ " + oldUriTEST.toString())

            println("reversing media url to check")
            val cursor: Cursor? = resolver
                .query(oldUriTEST!!,
                    arrayOf<String>(MediaStore.Images.ImageColumns.DATA), null, null, null)
            cursor!!.moveToFirst()
            val filePath = cursor.getString(0)
            println("Got reversed file path ~ " + filePath)
            cursor.close()

            try {
//                resolver.delete(
//                    oldUriTEST!!,
//                    MediaStore.MediaColumns.DATA + "='" + TESTOldFile + "'",
//                    null
//                )
            }
            catch(e: Exception){
                println("exception deleting test ~ " + e.printStackTrace())
            }

            println("Test delete done")

            val oldUri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
            println("Deleting original content uri for song ~ " + oldUri.toString())

//            resolver.delete(
//                oldUri!!,
//                MediaStore.MediaColumns.DATA + "=\"" + file.absolutePath + "\"",
//                null
//            )
            val contact_number = "4445555"
            val lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                contact_number
            )

            // The columns used for `Contacts.getLookupUri`
            val projection = arrayOf(
                ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
            )
            val data: Cursor? = resolver.query(lookupUri, projection, null, null, null)
            if (data != null && data.moveToFirst()) {

                try {
                    data.moveToFirst()
                    // Get the contact lookup Uri
                    val contactId = data.getLong(0)
                    val lookupKey = data.getString(1)

                    println("Uri String for Contact " + ContactsContract.Contacts.CONTENT_URI )
                    val contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey)
                    values.put(MediaStore.MediaColumns.DATA, file.absolutePath)
                    values.put(MediaStore.MediaColumns.TITLE, "RussianCircles CTITLE")
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
                    values.put(MediaStore.Audio.Media.IS_RINGTONE, true)

                    println("values put into ContentValues complete")

                    val uri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
                    val newUri = resolver.insert(uri!!, values)

                    if (newUri != null) {
                        val uriString = newUri.toString()

                        // Making duplicates for now 
//                        values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, uriString)
                        println("Uri String for Media " + uriString)
//                        val updated = resolver.update(contactUri, values, null, null).toLong()


                    }

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
//            Toast.makeText(this@RingtoneChange, "File does not exist", Toast.LENGTH_LONG).show()
        }
    }

    fun setContactNameByNumber(call_activity: AppCompatActivity) {
        val values = ContentValues()
        val resolver: ContentResolver = call_activity.applicationContext.contentResolver

        println("lookup contact by number")

        val contact_number = "4445555"
        val newName = "Some New Name"
        val lookupUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            contact_number
        )

        val projection = arrayOf(
            ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
        )
        val data: Cursor? = resolver.query(lookupUri, projection, null, null, null)
        if (data != null && data.moveToFirst()) {

            println("found contact ")
            data.moveToFirst()
            // Get the contact lookup Uri
            val contactId = data.getLong(0)
            val lookupKey = data.getString(1)

            println("contact id " + contactId.toString())

            val contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey)

            println("got contactUri " + contactUri.toString())

            val DATA_COLS = arrayOf(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.DATA1,  //phone number
                ContactsContract.Data.CONTACT_ID
            )

            val operations: ArrayList<ContentProviderOperation> = ArrayList()

            //selection for name
            //selection for name
            val where = java.lang.String.format(
                "%s = '%s' AND %s = ?",
                DATA_COLS[0],  //mimetype
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                DATA_COLS[2] /*contactId*/
            )


            val args = arrayOf<String>(contactId.toString())

            operations.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, args)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, newName)
                    .build()
            );

            //change selection for number
//                where = String.format(
//                    "%s = '%s' AND %s = ?",
//                    DATA_COLS[0],//mimetype
//                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
//                    DATA_COLS[1]/*number*/);
//                operations.add(
//                    ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
//                        .withSelection(where, args)
//                        .withValue(DATA_COLS[1]/*number*/, newNumber)
//                        .build()
//                );

            try {

                val results : Array<ContentProviderResult> =
                call_activity.applicationContext.getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY,
                    operations
                )

                for (result in results) {
                    println("Update Result" + result.toString());
                }

                data.close()
            }
            catch (e: Exception) {
                e.printStackTrace();
            }

        }
        else {
            println("contact not existing")
        }
    }


//    fun getContactId(context: Context?, number: String): String? {
//        if (context == null) return null
//        val cursor = context.contentResolver.query(
//            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//            arrayOf(
//                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
//                ContactsContract.CommonDataKinds.Phone.NUMBER
//            ),
//            ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
//            arrayOf(number),
//            null
//        )
//        if (cursor == null || cursor.count == 0) return null
//        cursor.moveToFirst()
//        val id =
//            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
//        cursor.close()
//        return id
//    }


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