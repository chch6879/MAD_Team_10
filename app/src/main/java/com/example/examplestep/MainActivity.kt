package com.example.examplestep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.examplestep.ui.screens.LoginScreen

import com.example.examplestep.ui.screens.HomeScreen
import com.example.examplestep.ui.screens.UniversitySelectionScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val universityViewModel: UniversityViewModel = viewModel()

            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(navController)
                }
//                composable("university_selection") {
//                    UniversitySelectionScreen(navController) { universityName ->
//                        // 선택한 대학 이름을 처리하는 로직 (예: Firebase에 저장)
//                        saveUniversityNameToFirebase(universityName)
//
//                        // 대학 선택 후 홈 화면으로 이동
//
//                    }
//                }
                composable("university_selection") {
                    UniversitySelectionScreen(navController, universityViewModel,) { universityName ->
                        // 대학 선택 후 추가 로직
                        navController.navigate("home")
                    }
                }
                composable("home") {
                    HomeScreen(navController)
                }
            }
        }
    }
}



