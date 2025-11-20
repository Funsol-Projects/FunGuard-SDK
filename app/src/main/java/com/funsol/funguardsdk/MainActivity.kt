package com.funsol.funguardsdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.funsol.funguardsdk.ui.theme.FunguardSDKAppTheme
import com.funsol.securitysdk.FunGuardSDK
import com.funsol.securitysdk.SecurityListener
import com.funsol.securitysdk.models.DialogConfig
import com.funsol.securitysdk.models.SecurityIssue
import com.funsol.securitysdk.models.SecurityResult

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FunguardSDKAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        FunGuardSDK.checkSecurity(
            this,
            rootCheck = true,
            tamperingCheck = true,
            fridaCheck = true,
            debuggerCheck = true,  // We're debugging, so skip this
            emulatorCheck = true,   // Allow testing on emulator
            showWarningDialog = true, // Show dialog if security check fails
            loggingEnabled = true, // Enable logging in debug mode
            dialogConfig = DialogConfig(supportEmail = "hanna@gmail.com"),
            listener = object: SecurityListener {
                override fun onSecurityCheckComplete(result: SecurityResult) {
                    // result.isSecure to check for results if you false the dialog
                }

                override fun onCancel(issueType: SecurityIssue) {
                }

            }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FunguardSDKAppTheme {
        Greeting("Android")
    }
}