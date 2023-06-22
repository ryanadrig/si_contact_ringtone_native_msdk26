package com.example.contactringtonenative

import android.database.Cursor

class SICCurseUtil {
    fun loopCurse(rt_data: Cursor){
        var rtd_ii = 0
        while (rtd_ii < rt_data.count) {
            println("Loop Cursor data not null")
            var col_idx = 0;
            for (col in rt_data.columnNames) {

                val columnKey: String = rt_data.getColumnName(col_idx)
                println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                println("LC col idx ~ " + col_idx.toString() + " LC Column name ~ " + columnKey)
                val rtt = rt_data.getType(col_idx)
                println("lc col type ~ " + rtt.toString())
                if (rtt == 0) {
                    val rtv = "null"
                    println("lc val ~ " + rtv)
                }
                if (rtt == 1) {
                    val rtv = rt_data.getInt(col_idx)
                    println("lc val ~ " + rtv.toString())
                }
                if (rtt == 2) {
                    val rtv = rt_data.getFloat(col_idx)
                    println("lc val ~ " + rtv.toString())
                }
                if (rtt == 3) {
                    val rtv = rt_data.getString(col_idx)
                    println("lc val ~ " + rtv)
                }
                if (rtt == 4) {
                    val rtv = rt_data.getBlob(col_idx)
                    println("lc val ~ " + rtv.toString())
                }
                col_idx += 1
            }
            rtd_ii++
            rt_data.moveToNext()
        }
    }
}