package com.example.casonaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.casonaapp.forgetPasswordView.ForgetPasswordView
import com.example.casonaapp.homeView.HomeView
import com.example.casonaapp.login.LoginScreen
import com.example.casonaapp.registerView.RegisterView
import com.example.casonaapp.ui.theme.CasonaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CasonaAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onForgotPasswordClick = { navController.navigate("forgot_password") },
                onRegisterClick = {navController.navigate("register")},
                onLoginSuccess = { navController.navigate("home") }
            )
        }
        composable("forgot_password") {
            ForgetPasswordView(onBack = { navController.popBackStack() })
        }

        composable("register") {
            RegisterView(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("home") }
            )
        }
        composable("home") {
            HomeView()
        }
    }
}