package com.example.examplestep

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.examplestep.ui.screens.LoginScreen
import android.Manifest
import com.example.examplestep.ui.screens.HomeScreen
import com.example.examplestep.ui.screens.RankingScreen
import com.example.examplestep.ui.screens.SplashScreen
import com.example.examplestep.ui.screens.UniversitySelectionScreen

class MainActivity : ComponentActivity() {

    private val stepcountViewModel: StepcountViewModel by viewModels()
//    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

/*        // ActivityResultLauncher를 통해 권한 요청 및 결과 처리
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                stepcountViewModel.startListening()
            }
        }

        // 권한이 없으면 요청하고, 있으면 바로 센서 시작
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            stepcountViewModel.startListening()
        }
*/
        stepcountViewModel.startListening()

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
                    HomeScreen(navController, userViewModel =userViewModel)
                }
                composable("ranking") {
                     RankingScreen(navController) // Ranking 화면 추가
                }
            }

        }


    }
}

