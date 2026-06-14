package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.navigation.NavGraph
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.CalendarViewModel
import com.example.myapplication.viewmodel.PlansViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MyApplication

        val authViewModel = AuthViewModel(app.authRepository)
        val calendarViewModel = CalendarViewModel(app.eventRepository)
        val plansViewModel = PlansViewModel(app.planRepository)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        calendarViewModel = calendarViewModel,
                        plansViewModel = plansViewModel
                    )
                }
            }
        }
    }
}
