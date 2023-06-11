package com.example.contactringtonenative

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.ContactsContract
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
        val data: Cursor? = resolver.query(lookupUri
            , projection, null, null, null)

        if (data != null && data.moveToFirst()) {

            println("found contact all proj data ")
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



}