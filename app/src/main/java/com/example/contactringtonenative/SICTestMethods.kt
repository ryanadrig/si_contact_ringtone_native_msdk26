package com.example.contactringtonenative

import android.app.Activity
import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract

class SICTestMethods {

    fun buildContactsList(call_activity: Activity){
        var bres: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

        val resolver: ContentResolver = call_activity!!.contentResolver

        val lookupUri = ContactsContract.Contacts.CONTENT_URI
        // Query Contact Data
        val ct_data_test: Cursor? = resolver.query(
            lookupUri!!, null, null, null, null)

        println("bcl cursor content uri ~ " + lookupUri.toString())

        println("begin cursor loop")

        ct_data_test!!.moveToFirst()
        val ct_data_len = ct_data_test!!.count
        var ct_idx= 0
        while (ct_idx < ct_data_len) {
            var col_idx = 0;
            for (col in ct_data_test.columnNames) {
                var brci : MutableMap<String, String> = mutableMapOf()
                val columnKey: String = ct_data_test.getColumnName(col_idx)
                println("loop col name ~ " + columnKey)
                println("loop col type ~ " + ct_data_test.getType(col_idx).toString())

                if (columnKey == "display_name") {
                    var columnVal = ct_data_test.getString(col_idx)
                    if (columnVal == null){
                        columnVal = "null"
                    }
                    brci.put(columnKey, columnVal)
                }
                    if (columnKey == "custom_ringtone") {
                        var columnVal = ct_data_test.getString(col_idx)
                        if (columnVal == null){
                            columnVal = "null"
                        }
                        brci.put(columnKey, columnVal)
                    }
                    if (columnKey == "_id"){
                        println("found id looping for number")
                        val columnVal = ct_data_test.getInt(col_idx)
                        val phoneCursor: Cursor? = resolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + columnVal,
                            null,
                            null
                        )
                            println("passed here")
                        while (phoneCursor!!.moveToNext()) {
                            val phone = phoneCursor.getString(
                                phoneCursor
                                    .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            )
                            if (phone == null){
                                println("phone null skipping add")
                            }else {
                                println("Got phone number ~ " + phone)
                                brci.put("phoneNumber", phone.toString())
                            }
                        }


                }
                if (brci.isEmpty() == false) {
                    if (bres[ct_idx.toString()] == null){
                        bres[ct_idx.toString()] = mutableMapOf()
                    }
                    bres[ct_idx.toString()]!!.putAll(brci)
                }
                col_idx += 1
            }
            ct_idx += 1
            ct_data_test.moveToNext()

        }
        ct_data_test!!.close()

        println("build contact list complete" + bres.toString())
    }
}