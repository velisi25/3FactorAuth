package com.example.a3factorauthentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a3factorauthentication.ui.theme._3FactorAuthenticationTheme
import java.time.LocalDate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.format.DateTimeFormatter


data class User(
    val name: String,
    val birthDate: String,
    val userName: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)


class RegistrationPage : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _3FactorAuthenticationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.purple_200)
                ) {
                    RegPage(this@RegistrationPage)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegPage(activity: ComponentActivity){
    Log.d("RegPage","Regpage function called")
    Scaffold(
        topBar = {
            TopAppBar(
                title={Text(text="Register your account here...")},
                navigationIcon = { IconButton(onClick = {
                    val context = activity
                    val navigate = Intent(context,MainActivity::class.java)
                    context.startActivity(navigate)

                }) {
                    Icon(Icons.Filled.ArrowBack,contentDescription = null)
                }
                }
            )
        },
        content={
            FormPage(activity)
        }
    )
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FormPage(activity: ComponentActivity){
    Log.d("FormPage","Formpage function called")
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        TextField(activity)
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TextField(activity: ComponentActivity){
    Column(modifier=Modifier.padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        var text by remember { mutableStateOf(TextFieldValue("")) }
        var isError by remember { mutableStateOf(true) }
        OutlinedTextField(
            value = text,
            leadingIcon = {Icon(painterResource(id = R.drawable.badge),contentDescription = null)},
            onValueChange = { newText -> text = newText
                            isError = false},
            label = { Text(text = "Full Name") },
            placeholder = { Text(text = "Enter your full name here")},
            isError = isError
        )
        if (text.text.isNotEmpty() && isError) {
            Text(
                text = "Name field cannot be empty"
            )
        }
        Spacer(modifier = Modifier.padding(3.dp))
        var selectedDate by remember{ mutableStateOf<LocalDate?>(null) }
        val calendarState = rememberSheetState()
        LaunchedEffect(selectedDate) {
            Log.d("SelectedDate", "Date changed: $selectedDate")
        }

        CalendarDialog(
            state = calendarState ,
            config = CalendarConfig(
                monthSelection = true,
                yearSelection = true
            ),
            selection = CalendarSelection.Date { localDate ->
                selectedDate = localDate
            }
        )
        Button(
            onClick = { calendarState.show()},
            border = BorderStroke(1.dp,Color.Gray),
            shape = RectangleShape,
            modifier = Modifier.size(width=280.dp,height=45.dp),
            colors = ButtonDefaults.buttonColors(Color.White)
        ) {
            Icon(
                Icons.Rounded.DateRange,
                tint = Color.Black,
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = selectedDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                    ?: "Enter Date of Birth",
                color = Color.Black,
                textAlign = TextAlign.Start
            )
        }


        var text2 by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            value = text2,
            leadingIcon = {Icon(Icons.Rounded.Person,contentDescription = null)},
            onValueChange = { text2 = it },
            label = { Text(text = "UserName") },
            placeholder = { Text(text = "Enter your Username") }
        )
        var text3 by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            value = text3,
            leadingIcon = {Icon(Icons.Rounded.Email,contentDescription = null)},
            onValueChange = { text3 = it },
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Enter your emailid ") }
        )
        var text4 by rememberSaveable { mutableStateOf("") }
        var text4Visibility by remember{ mutableStateOf(false) }
        var isPasswordValid by remember(text4) {
            mutableStateOf(checkPasswordValidation(text4))
        }

        val icon = if(text4Visibility){
            painterResource(id = R.drawable.visibility)
        }
        else{
            painterResource(id = R.drawable.visibilityoff)
        }
        OutlinedTextField(
            value = text4,
            leadingIcon = {Icon(Icons.Rounded.Lock,contentDescription = null)},
            onValueChange = {newValue -> text4 = newValue
                            isPasswordValid= checkPasswordValidation(text4)
            },
            label = { Text(text = "Password") },
            placeholder = { Text(text = "Choose a password ") },
            isError = text4.isNotEmpty() && !isPasswordValid,
            trailingIcon = {
                IconButton(onClick = { text4Visibility = !text4Visibility  }) {
                    Icon(
                        painter = icon,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if(text4Visibility) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

        )
        if (text4.isNotEmpty() && !isPasswordValid) {
            Text(
                text = "Password must have at least one capital letter, one special character, and password length of 8",
            )
        }
        var text5 by rememberSaveable { mutableStateOf("") }
        var text5Visibility by remember{ mutableStateOf(false) }
        var arePasswordsMatching by remember(text4, text5) {
            mutableStateOf(text4 == text5)
        }

        val icon1 = if(text5Visibility){
            painterResource(id = R.drawable.visibility)
        }
        else{
            painterResource(id = R.drawable.visibilityoff)
        }

        OutlinedTextField(
            value = text5,
            leadingIcon = {Icon(painter= painterResource(id = R.drawable.doneall),contentDescription = null)},
            onValueChange = { newValue -> text5=newValue
                            arePasswordsMatching = text4 == text5},
            label = { Text(text = "Confirm Password") },
            placeholder = { Text(text = "Re-enter your password ") },
            trailingIcon = {
                IconButton(onClick = { text5Visibility = !text5Visibility  }) {
                    Icon(
                        painter = icon1,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if(text5Visibility) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = text5.isNotEmpty() && !arePasswordsMatching
        )
        if (text5.isNotEmpty() && !arePasswordsMatching) {
            Text(
                text = "Password doesn't match",
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        val checkedState = remember {
            mutableStateOf(true)
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = {checkedState.value = it},
                modifier = Modifier.padding(16.dp)
            )
            Text(text = "I accept all terms & conditions.",modifier=Modifier.padding(20.dp))
        }


        Row(modifier= Modifier
            .padding(16.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                text = TextFieldValue("")
                selectedDate = null
                text2 = TextFieldValue("")
                text3 = TextFieldValue("")
                text4 = ("")
                text5 = ("")
                checkedState.value = false
            },colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text(
                    text = stringResource(id = R.string.clear_btn),
                    fontSize = 25.sp,
                    color = Color.White
                )
            }
            Button(onClick = {
                Log.d("ButtonClick","Button Clicked")
                if (text.text.isNotEmpty() && text4.isNotEmpty() && text5.isNotEmpty() && text4 == text5 && checkedState.value) {
                    Log.d("ButtonClick","All conditions met,processing")
                    val database = FirebaseDatabase.getInstance()
                    val usersRef = database.getReference("users")
                    val newUserRef = usersRef.push()
                    val user = User(
                        name=text.text,
                        birthDate = selectedDate.toString(),
                        userName = text2.text,
                        email = text3.text,
                        password = text4,
                        confirmPassword = text5
                    )
                    newUserRef.setValue(user)


                    val context = activity
                    val intent = Intent(context,PhoneNoVerification::class.java)
                    ContextCompat.startActivity(context,intent,null)

                }
                else{
                    Log.d("ButtonClick","Conditions doesn't met")
                }
            },colors = ButtonDefaults.buttonColors(Color.Black)) {
                Text(
                    text = stringResource(id = R.string.submit_btn),
                    fontSize = 25.sp,
                    color = Color.White

                )
            }
        }
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


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val context = LocalContext.current
    _3FactorAuthenticationTheme {
        RegPage(context as ComponentActivity)
    }
}