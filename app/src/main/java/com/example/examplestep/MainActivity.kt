package com.example.examplestep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.examplestep.ui.screens.LoginScreen

import com.example.examplestep.ui.screens.HomeScreen
import com.example.examplestep.ui.screens.RankingScreen
import com.example.examplestep.ui.screens.SplashScreen
import com.example.examplestep.ui.screens.UniversitySelectionScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val universityViewModel: UniversityViewModel = viewModel()
            val userViewModel: UserViewModel= viewModel()


            NavHost(
                navController = navController,
                startDestination = "splash"
            ) {
                composable("splash"){
                    SplashScreen(navController)
                }
                composable("login") {
                    LoginScreen(navController)
                }
                composable("university_selection") {
                    UniversitySelectionScreen(navController, universityViewModel,) { universityName ->
                        // 대학 선택 후 추가 로직
                        navController.navigate("home")
                    }
                }
                composable("home") {
                    HomeScreen(navController,userViewModel=userViewModel)
                }
                composable("ranking") {
                     RankingScreen(navController) // Ranking 화면 추가
                }
            }

        }


    }
}


