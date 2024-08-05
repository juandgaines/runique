package com.juandgaines.runique

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.juandgaines.core.presentation.designsystem.RuniqueTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.state.isCheckingAuth }
        }
        setContent {
            RuniqueTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    if(!viewModel.state.isCheckingAuth){
                        val navHostController = rememberNavController()
                        NavigationRoot(
                            navHostController,
                            isLoggedIn = viewModel.state.isLoggedIn
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RuniqueTheme {
        Greeting("Android")
    }
}