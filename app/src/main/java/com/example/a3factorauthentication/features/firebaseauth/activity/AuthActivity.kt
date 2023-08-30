package com.example.a3factorauthentication.features.firebaseauth.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import com.example.a3factorauthentication.features.firebaseauth.screens.PhoneNoVerification
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            androidx.compose.material3.Surface() {
                Scaffold {
                    PhoneNoVerification(activity = this)
            }
            }
        }
    }
}


