package com.example.a3factorauthentication.utils

import android.content.Context
import android.widget.Toast


fun Context.showMsg(
    msg:String,
    duration:Int = Toast.LENGTH_SHORT
) = Toast.makeText(this,msg,duration).show()