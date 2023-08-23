package com.example.a3factorauthentication

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a3factorauthentication.ui.theme._3FactorAuthenticationTheme
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

val LocalActivity = compositionLocalOf<ComponentActivity?> { null }

class PhoneNoVerification:ComponentActivity(){
    lateinit var auth:FirebaseAuth
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            _3FactorAuthenticationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.purple_200)
                ) {
                        PhoneNoVerify(this)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNoVerify(activity: ComponentActivity){
    val activity = LocalContext.current as ComponentActivity
    CompositionLocalProvider(LocalActivity provides activity) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Phonenumber Verification") }
                )
            },
            content = {
                Verify()
            }
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Verify(){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Verification()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Verification() {
    val auth = FirebaseAuth.getInstance()
    var showOtpDialog by remember{ mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var phoneno by remember { mutableStateOf(TextFieldValue("+91")) }
        OutlinedTextField(
            value = phoneno,
            onValueChange = { phoneno = it },
            leadingIcon = { Icon(Icons.Rounded.Call, contentDescription = null) },
            label = { Text(text = "Phone Number") },
            placeholder = { Text(text = "Enter your phone number") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        val currentActivity = LocalActivity.current
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.End) {
            Button(onClick = {
                if(currentActivity!= null) {
                    initiatePhoneAuth(auth, phoneno.text, currentActivity) { vId ->
                        verificationId = vId
                        showOtpDialog = true
                    }
                }
            },colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text(text = "Get OTP", fontSize = 15.sp)

            }
        }
        if(showOtpDialog){
            OtpDialog(
                auth=auth,
                verificationId=verificationId,
                otp=otp,
                onDismiss = {showOtpDialog=false },
                onVerifyClick = {
                    val coroutineScope = CoroutineScope(Dispatchers.Main)
                    coroutineScope.launch { verifyOtp(auth,verificationId,otp,context) }
                     })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpDialog(auth: FirebaseAuth,verificationId: String,otp:String,onDismiss: () -> Unit,onVerifyClick:()->Unit) {
    val context = LocalContext.current
    val otpStringBuilder = remember{StringBuilder(otp)}
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Enter OTP", fontSize = 20.sp) },
        confirmButton = {
            Button(
                onClick = {
                    verifyOtp(auth,verificationId,otpStringBuilder.toString(),context )
                          onVerifyClick()
                          },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Verify", fontSize = 15.sp)
            }
        },
        text = {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val otpFields = List(6) {FocusRequester()}
                for(index in otpFields.indices){
                    OutlinedTextField(
                        value = otpStringBuilder.getOrNull(index)?.toString()?:"",
                        onValueChange = {newValue->
                            if (index < otpFields.size -1 && newValue.isNotEmpty()) {
                                otpFields[index+1].requestFocus()
                            }
                            if (newValue.length <= 1) {
                                otpStringBuilder.replace(index, index + 1, newValue)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .focusRequester(otpFields[index])
                    )
                }
            }
        }
    )
}


private fun initiatePhoneAuth(
    auth:FirebaseAuth, phoneNumber: String, currentActivity: ComponentActivity,onVerficationIdSent:(String)->Unit) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(currentActivity)// Pass your activity instance here
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Automatically handle verification for testing
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // Handle verification failure
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // Store verificationId to use when verifying the OTP
                    onVerficationIdSent(verificationId)
                    println(verificationId)
                    // You can also store the verificationId in a ViewModel or similar
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
}

private fun verifyOtp(auth: FirebaseAuth,verificationId: String, otp: String,context: Context) {
    val credential = PhoneAuthProvider.getCredential(verificationId, otp)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showSuccessToast(context,"OTP verification successful!")
            } else {
                val exception = task.exception
                showFailureToast(context,"OTP verification failed: ${exception?.message}.")
            }
        }
}


private fun showSuccessToast(context:Context,message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


private fun showFailureToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PhoneNoVerificationPreview(){
    val context = LocalContext.current
    _3FactorAuthenticationTheme {
        PhoneNoVerify(context as ComponentActivity)
    }
}


