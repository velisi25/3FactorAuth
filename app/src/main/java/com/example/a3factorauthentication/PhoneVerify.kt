package com.example.a3factorauthentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.a3factorauthentication.ui.theme._3FactorAuthenticationTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PhoneVerify: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent {
            _3FactorAuthenticationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.lavender)
                ) {
                    Verification()
                }
            }
        }
    }
}

@Composable
fun HeaderComponents(
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
fun Verification(){
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val usersRef = database.getReference("users")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HeaderComponents(
            header = {
                Text(
                    text = "Phone Number Verification",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            },
            contentHorizontalPadding = 16
        )

        Spacer(modifier = Modifier.height(16.dp))
        var phoneNo by remember{ mutableStateOf("") }
        OutlinedTextField(
            value = phoneNo ,
            onValueChange ={ phoneNo = it},
            label = { Text(text = "Phone Number") },
            placeholder = { Text(text = "Enter the phone number")},
            leadingIcon = { Icon(Icons.Rounded.Phone,contentDescription=null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = {
            val enteredPhoneNumber = phoneNo.trim()
            val query = usersRef.orderByChild("phoneNumber").equalTo("+91$enteredPhoneNumber")
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(userSnapShot in snapshot.children){
                            val user = userSnapShot.getValue(User::class.java)
                            if(user?.phoneNumber == "+91$enteredPhoneNumber"){
                                val intent = Intent(context,MainActivity::class.java)
                                context.startActivity(intent)
                            }else{
                                val toast = Toast.makeText(context,"Phonenumber doesn't match", Toast.LENGTH_SHORT)
                                toast.show()
                            }
                        }
                    }else{
                        val toast = Toast.makeText(context, "Phone number not found", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    val errorMessage = "Database Error: ${error.message}"
                    val toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                    toast.show()
                }

            })
        }) {
            Text(
                text = "Verify",
                color = Color.White)
            
        }
    }

}