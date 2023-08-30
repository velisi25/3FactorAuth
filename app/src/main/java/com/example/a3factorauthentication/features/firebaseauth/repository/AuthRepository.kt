package com.example.a3factorauthentication.features.firebaseauth.repository

import android.app.Activity
import com.example.a3factorauthentication.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {


    fun createUserWithPhone(
        phone:String,
        activity:Activity
    ) : Flow<ResultState<String>>

    fun signWithCredential(
        otp:String
    ): Flow<ResultState<String>>

}