package com.example.a3factorauthentication

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.a3factorauthentication.ui.theme._3FactorAuthenticationTheme
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import android.content.Context
import android.app.Activity
import android.widget.Toast
import com.example.a3factorauthentication.utils.getActivity

class PhoneNoVerification : ComponentActivity(){
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _3FactorAuthenticationTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.purple_200)
                ){
                    LoginDialog()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginDialog(){
    val dialogState : MutableState<Boolean> = remember{ mutableStateOf(true) }
    Dialog(
        onDismissRequest = { dialogState.value = false },
        content = { CompleteDialogContent()},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}

val auth = FirebaseAuth.getInstance()
var storedVerificationId: String = ""

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CompleteDialogContent(){
    val context = LocalContext.current
    var phoneno by remember{ mutableStateOf(TextFieldValue("")) }
    var otp by remember{ mutableStateOf(TextFieldValue("")) }
    var isOtpVisible by remember{ mutableStateOf(false) }
    Card(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth(1f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(1f)
                .wrapContentHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Enter your phone number", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            OutlinedTextField(
                value = phoneno,
                onValueChange = {if(it.text.length<=10) phoneno = it} ,
                label = { Text(text = "Phone Number")},
                placeholder = { Text(text = "Enter the phone number")},
                leadingIcon = {Icon(Icons.Rounded.Phone,contentDescription=null)},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(top = 4.dp),
                singleLine = true,
            )
            if(isOtpVisible){
                TextField(
                    value = otp,
                    onValueChange = {otp=it},
                    placeholder = { Text(text = "Enter otp")} ,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(top = 4.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
            }

            if(!isOtpVisible) {
                Button(
                    onClick = { onLoginClicked(context,phoneno.text) {
                        Log.d("phoneBook","setting otp visible")
                        isOtpVisible = true
                    }
                    },
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(top = 8.dp)
                ) {
                    Text(text = "Send OTP")
                }
            }

            if(isOtpVisible) {
                Button(
                    onClick = { verifyPhoneNumberWithCode(context, storedVerificationId,otp.text) },
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(top = 8.dp)
                ) {
                    Text(text = "Verify", color = Color.White)
                }
            }
        }
    }
}

private fun verifyPhoneNumberWithCode(context: Context, verificationId: String, code: String) {
    val credential = PhoneAuthProvider.getCredential(verificationId, code)
    signInWithPhoneAuthCredential(context,credential)
}

fun onLoginClicked (context: Context, phoneNumber: String,onCodeSent: () -> Unit) {

    try {
        auth.setLanguageCode("en")
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("phoneBook", "verification completed")
                signInWithPhoneAuthCredential(context, credential)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.d("phoneBook", "verification failed" + p0)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("phoneBook", "code sent" + verificationId)
                storedVerificationId = verificationId
                onCodeSent()
            }

        }
        val options = context.getActivity()?.let {
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+91" + phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(it)
                .setCallbacks(callback)
                .build()
        }

        if (options != null) {
            Log.d("phoneBook", options.toString())
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }catch(e:Exception){
        Log.e("phoneBook", "Error during login: ${e.message}", e)
    }
}

private fun signInWithPhoneAuthCredential(context: Context, credential: PhoneAuthCredential) {
        try {
            context.getActivity()?.let {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(it) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = task.result?.user
                            Log.d("phoneBook", "logged in")
                            val toast = Toast.makeText(context,"OTP verified successfully!!!",
                                Toast.LENGTH_SHORT)
                            toast.show()
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Log.d("phoneBook", "wrong otp")
                                val toast = Toast.makeText(context,"Wrong OTP",Toast.LENGTH_SHORT)
                                toast.show()
                            }
                            // Update UI
                        }
                    }
            }
        }catch(e:Exception){
            Log.e("phoneBook", "Error during sign-in: ${e.message}", e)
        }
}

@Preview
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginDialogPreview(){
    CompleteDialogContent()
}