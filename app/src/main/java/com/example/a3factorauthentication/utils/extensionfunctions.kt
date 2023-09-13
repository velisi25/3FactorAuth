package com.example.a3factorauthentication.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast


fun Context.showMsg(
    msg:String,
    duration:Int = Toast.LENGTH_SHORT
) = Toast.makeText(this,msg,duration).show()

fun Context.getActivity(): Activity?= when(this){
    is Activity-> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}