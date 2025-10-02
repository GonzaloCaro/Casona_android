package com.example.casonaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.casonaapp.forgetPasswordView.ForgetPasswordView
import com.example.casonaapp.homeView.HomeView
import com.example.casonaapp.login.LoginScreen
import com.example.casonaapp.registerView.RegisterView
import com.example.casonaapp.ui.theme.CasonaAppTheme
import com.example.casonaapp.viewmodels.FontSizeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.casonaapp.viewmodels.LocalFontSize
import com.example.casonaapp.viewmodels.ThemeViewModel
import androidx.compose.material3.Surface
import com.example.casonaapp.eventDetails.EventDetailsView
import com.example.casonaapp.eventManagement.CreateEditEventView
import com.example.casonaapp.eventManagement.EventManagementView
import com.example.casonaapp.Profile.ProfileView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val fontSizeViewModel: FontSizeViewModel = viewModel()

            CasonaAppTheme(themeViewModel = themeViewModel) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        fontSizeViewModel = fontSizeViewModel,
                        themeViewModel = themeViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    fontSizeViewModel: FontSizeViewModel,
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()
    val fontSize = fontSizeViewModel.fontSize.collectAsState()

    CompositionLocalProvider(LocalFontSize provides fontSize.value) {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onForgotPasswordClick = { navController.navigate("forgot_password") },
                    onRegisterClick = { navController.navigate("register") },
                    onLoginSuccess = { navController.navigate("home") },
                    viewModel = fontSizeViewModel,
                    themeViewModel = themeViewModel,
                )
            }
            composable("forgot_password") {
                ForgetPasswordView(
                    viewModel = fontSizeViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("register") {
                RegisterView(
                    viewModel = fontSizeViewModel,
                    onBack = { navController.popBackStack() },
                    onRegisterSuccess = { navController.navigate("home") }
                )
            }
            composable("home") {
                HomeView(
                    viewModel = fontSizeViewModel,
                    navController = navController
                )
            }

            composable("profile") {
                ProfileView(onBack = { navController.popBackStack() })
            }

            composable("eventManagement") {
                EventManagementView(navController = navController)
            }

            composable("createEditEvent") {
                CreateEditEventView(navController = navController)
            }

            composable("createEditEvent/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                CreateEditEventView(
                    navController = navController,
                    eventId = eventId
                )
            }

            composable("eventDetails/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                EventDetailsView(
                    navController = navController,
                    eventId = eventId ?: ""
                )
            }
        }
    }
}