package com.example.contactringtonenative

import android.app.Activity
import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import com.google.gson.Gson



//import org.json.JSONObject

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
                val loop_col_type : Int = ct_data_test.getType(col_idx)
                println("loop col type ~ " + loop_col_type.toString())
                if (loop_col_type == 0) {
                    println("loop col val ~ null")
                }
                if (loop_col_type == 1) {
                    val rtv = ct_data_test.getInt(col_idx)
                    println("loop col val ~ " + rtv.toString())
                }
                if (loop_col_type == 2) {
                    val rtv = ct_data_test.getFloat(col_idx)
                    println("loop col val ~ " + rtv.toString())
                }
                if (loop_col_type == 3) {
                    val rtv = ct_data_test.getString(col_idx)
                    println("loop col val ~ " + rtv)
                }


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

                        println("contact id col loop")
                        var cid_idx : Int = 0
//                        while (phoneCursor!!.moveToNext()) {
                            println("looping contact id col ~ " + phoneCursor!!.getColumnName(cid_idx))
                            phoneCursor.moveToFirst()

                        val pcp = phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        println("get col of phone number real quick ~ " + pcp.toString())
                        SICCurseUtil().loopCurse(phoneCursor!!)
                        //                            val phone = phoneCursor.getString(
//                                phoneCursor
//                                    .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
//                            )
//                            if (phone == null){
//                                println("phone null skipping add")
//                            }else {
//                                println("Got phone number ~ " + phone)
//                                brci.put("phoneNumber", phone.toString())
//                            }
//                        }


                }
                if (columnKey == "display_name_reverse"){
                    var columnVal = ct_data_test.getString(col_idx)
                    if (columnVal == null){
                        columnVal = "null"
                    }
                    brci.put(columnKey, columnVal.toString())
                }
                if (columnKey == "photo_thumb_uri"){
                    var columnVal = ct_data_test.getString(col_idx)
                    if (columnVal == null){
                        columnVal = "null"
                    }
                    brci.put(columnKey, columnVal.toString())
                }
                if (columnKey == "photo_uri"){
                    var columnVal = ct_data_test.getString(col_idx)
                    if (columnVal == null){
                        columnVal = "null"
                    }
                    brci.put(columnKey, columnVal.toString())
                }
                if (columnKey == "photo_file_id"){
                    var columnVal = ct_data_test.getInt(col_idx)
                    if (columnVal == null){
                        columnVal = 0
                    }
                    brci.put(columnKey, columnVal.toString())
                }
                if (columnKey == "photo_id"){
                    var columnVal  = ct_data_test.getInt(col_idx)
                    if (columnVal == null){
                        columnVal = 0
                    }
                    brci.put(columnKey, columnVal.toString())
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

        val gson = Gson()
        val json = gson.toJson(bres)
        println("json convert complete")
        println("to gson ~ " + json.toString())
//        val jsonres: JSONObject = JSONObject(bres.toString())
//        println("convert to json object success")
//        println("json obj ~ " + jsonres.toString())
    }



}