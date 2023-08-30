package com.example.a3factorauthentication.features.firebaseauth.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.a3factorauthentication.features.firebaseauth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo:AuthRepository
) : ViewModel() {


    fun createUserWithPhone(
        mobile:String,
        activity:Activity
    ) = repo.createUserWithPhone(mobile,activity)

    fun signInWithCredential(
        code:String
    ) = repo.signWithCredential(code)

}

