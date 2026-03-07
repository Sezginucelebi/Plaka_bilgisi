package com.sezgin.plaka_bilgisi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sezgin.plaka_bilgisi.R
import com.sezgin.plaka_bilgisi.ui.screens.*
import com.sezgin.plaka_bilgisi.ui.theme.PlakaBilgisiTheme

class MainActivity : ComponentActivity() {

    private lateinit var googleSignInClient: com.google.android.gms.auth.api.signin.GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val rememberMe = sharedPref.getBoolean("rememberMe", false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            PlakaBilgisiTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(navController = navController)
                    }

                    composable("entry") {
                        EntryScreen(
                            onPersonalClick = { navController.navigate("login") },
                            onCorporateClick = { navController.navigate("corporateLogin") }
                        )
                    }

                    composable("corporateLogin") {
                        CorporateLoginScreen(
                            onLoginSuccess = {
                                navController.navigate("dashboard") {
                                    popUpTo("entry") { inclusive = true }
                                }
                            },
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("login") {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            LoginScreen(
                                onLoginClick = { email, password, remember ->
                                    auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                if (remember) {
                                                    sharedPref.edit()
                                                        .putString("email", email)
                                                        .putString("password", password)
                                                        .putBoolean("rememberMe", true)
                                                        .apply()
                                                } else {
                                                    sharedPref.edit().clear().apply()
                                                }
                                                navController.navigate("dashboard") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            } else {
                                                Toast.makeText(this@MainActivity, "Giriş başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                },
                                onRegisterClick = {
                                    navController.navigate("register")
                                },
                                onForgotPasswordClick = {
                                    navController.navigate("forgot")
                                },
                                rememberMe = rememberMe,
                                onGoogleLoginClick = {
                                    val signInIntent = googleSignInClient.signInIntent
                                    startActivityForResult(signInIntent, RC_SIGN_IN)
                                }
                            )
                        }
                    }

                    composable("dashboard") {
                        DashboardScreen(
                            onVehicleRegisterClick = { navController.navigate("vehicleRegister") },
                            onVehicleSearchClick = { navController.navigate("vehicleSearch") },
                            onAdminClick = { navController.navigate("adminDashboard") },
                            onProfileClick = { navController.navigate("profile") },
                            onLogoutClick = {
                                auth.signOut()
                                navController.navigate("entry") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("profile") {
                        ProfileScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            navController = navController,
                            onRegisterSuccess = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            onLoginClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("forgot") {
                        ForgotPasswordScreen(
                            navController = navController,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("resetPassword") {
                        ResetPasswordScreen(
                            navController = navController,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                    
                    composable("vehicleRegister") {
                        VehicleRegisterScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                    
                    composable("vehicleSearch") {
                        VehicleSearchScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("adminDashboard") {
                        AdminDashboardScreen(
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onUserManagementClick = {
                                navController.navigate("userManagement")
                            }
                        )
                    }

                    composable("userManagement") {
                        UserManagementScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Google ile giriş başarılı
                        } else {
                            // Giriş hatası
                        }
                    }
            } catch (e: ApiException) {
                // Google Sign-In hatası
            }
        }
    }
}
