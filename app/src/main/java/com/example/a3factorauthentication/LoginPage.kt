package com.example.a3factorauthentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a3factorauthentication.ui.theme._3FactorAuthenticationTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginPage:ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent {
            _3FactorAuthenticationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.lavender)
                ) {
                    SignIn(this@LoginPage)
                }
            }
        }
    }
}

@Composable
fun HeaderComponent(
    header: @Composable () -> Unit,
    contentHorizontalPadding: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = contentHorizontalPadding.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        header()
    }
}

@Composable
fun SignIn(activity:ComponentActivity){
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        HeaderComponent(
            header = {
                     Text(
                         text = "Login Page",
                         style = MaterialTheme.typography.headlineMedium,
                         textAlign = TextAlign.Center
                     )
            },
            contentHorizontalPadding = 16
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        var username by remember{ mutableStateOf("") }
        OutlinedTextField(
            value = username,
            onValueChange = {username=it},
            leadingIcon = {Icon(Icons.Rounded.Person ,contentDescription = null)},
            label = { Text(text = "Username")},
            placeholder = { Text(text = "Enter your user id")},
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        var pwd by remember{ mutableStateOf("") }
        var pwdVisibility by remember{ mutableStateOf(false) }
        var isPasswordValid by remember(pwd) {
            mutableStateOf(checkPasswordValidation(pwd))
        }

        val icon = if(pwdVisibility){
            painterResource(id = R.drawable.visibility)
        }
        else{
            painterResource(id = R.drawable.visibilityoff)
        }

        OutlinedTextField(
            value = pwd,
            onValueChange = {newValue -> pwd = newValue},
            leadingIcon = {Icon(Icons.Rounded.Lock,contentDescription = null)},
            label = { Text(text = "Password") },
            placeholder = { Text(text = "Enter the password ") },
            trailingIcon = {
                IconButton(onClick = { pwdVisibility = !pwdVisibility  }) {
                    Icon(
                        painter = icon,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if(pwdVisibility) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
        )
        if (pwd.isNotEmpty() && !isPasswordValid) {
            Text(
                text = "Password must have at least one capital letter, one special character, and password length of 8",
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val enteredUsername = username
            val enteredPassword = pwd

            val query = usersRef.orderByChild("userName").equalTo(enteredUsername)

            query.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(userSnapShot in snapshot.children){
                            val user = userSnapShot.getValue(User::class.java)
                            if(user?.password == enteredPassword){
                                val context = activity
                                val intent = Intent(context,PhoneVerify::class.java)
                                context.startActivity(intent)
                            }else{
                                val toast = Toast.makeText(context,"Passwords doesn't match",Toast.LENGTH_SHORT)
                                toast.show()
                            }
                        }
                    }else{
                        val toast = Toast.makeText(context,"Username doesn't exist",Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        },colors = ButtonDefaults.buttonColors(Color.Black)) {
            Text(
                text = "Login",
                color = Color.White
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    append("New User? Register")
                    addStringAnnotation(
                        tag = "clickable",
                        annotation = "registration",
                        start = 0,
                        end = length
                    )
                }
            },
            modifier = Modifier.clickable {
                val context = activity
                val navigate = Intent(context,RegistrationPage::class.java)
                context.startActivity(navigate) // Navigate to the registration screen
            },
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )


    }

}

private fun checkPasswordValidation(password: String): Boolean {
    val capitalLetterPattern = Regex("[A-Z]")
    val specialCharacterPattern = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")
    val digitPattern = Regex("\\d")
    val lengthPattern = Regex(".{8,}")

    val hasCapitalLetter = capitalLetterPattern.containsMatchIn(password)
    val hasSpecialCharacter = specialCharacterPattern.containsMatchIn(password)
    val hasDigits = digitPattern.containsMatchIn(password)
    val hasMinLength = lengthPattern.containsMatchIn(password)

    return hasCapitalLetter && hasSpecialCharacter && hasDigits && hasMinLength
}

@Preview
@Composable
fun SignInPreview(){
    val context = LocalContext.current
    SignIn(context as ComponentActivity)
}