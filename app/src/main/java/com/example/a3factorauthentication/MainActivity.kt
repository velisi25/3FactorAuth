package com.example.a3factorauthentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.a3factorauthentication.ui.theme._3FactorAuthenticationTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _3FactorAuthenticationTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.lavender)
                ) {
                    HomePage(this@MainActivity)
                }
            }
        }
    }
}

@Composable
fun HomePage(activity:ComponentActivity){
    Column(modifier= Modifier
        .fillMaxWidth()
        .fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Image(
            painter = painterResource(id = R.drawable.login1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(300.dp)
                .padding(top = 15.dp, bottom = 15.dp)

        )
        Button(onClick = {
            val context = activity
            val navigate = Intent(context ,RegistrationPage::class.java )
            context.startActivity(navigate)
        }, colors = ButtonDefaults.buttonColors(Color.Black), modifier = Modifier
            .padding(top = 16.dp, bottom = 16.dp)
            .align(alignment = Alignment.CenterHorizontally)) {
            Text(
                text = stringResource(id = R.string.sign_up),
                fontSize = 25.sp,
                color = Color.White,
            )
        }
        Button(onClick = {
            val context = activity
            val navigate = Intent(context,LoginPage::class.java)
            context.startActivity(navigate)
        },colors = ButtonDefaults.buttonColors(Color.Black), modifier = Modifier.align(alignment=Alignment.CenterHorizontally)) {
            Text(
                text = stringResource(id = R.string.sign_in),
                fontSize = 25.sp,
                color = Color.White

            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val context = LocalContext.current
    _3FactorAuthenticationTheme {
        HomePage(context as ComponentActivity)
    }
}
