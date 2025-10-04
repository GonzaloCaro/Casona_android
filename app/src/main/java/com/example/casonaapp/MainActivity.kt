package com.example.casonaapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
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
import com.example.casonaapp.userManagment.CreateEditUserView
import com.example.casonaapp.userManagment.UserManagementView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    companion object {
        private const val AUDIO_PERMISSION_REQUEST_CODE = 123
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AUDIO_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido
                    Toast.makeText(this, "Permiso de micrófono concedido", Toast.LENGTH_SHORT).show()
                } else {
                    // Permiso denegado
                    Toast.makeText(this, "Permiso de micrófono denegado", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

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

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val startDestination = if (currentUser != null) "home" else "login"

    CompositionLocalProvider(LocalFontSize provides fontSize.value) {
        NavHost(navController = navController, startDestination = startDestination) {

            composable("login") {
                LoginScreen(
                    onForgotPasswordClick = { navController.navigate("forgot_password") },
                    onRegisterClick = { navController.navigate("register") },
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
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
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
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

            composable("userManagement") {
                UserManagementView(navController = navController)
            }

            composable("createEditUser") {
                CreateEditUserView(navController = navController)
            }

            composable("createEditUser/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                CreateEditUserView(
                    navController = navController,
                    userId = userId
                )
            }
        }
    }
}
