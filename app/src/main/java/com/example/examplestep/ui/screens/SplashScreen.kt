package com.example.examplestep.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.ui.screens.LoginScreen
import com.example.examplestep.ui.screens.HomeScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(true) {
        // 1초 정도 기다린 후 로그인 상태 확인
        delay(1000)

        // 로그인된 사용자가 있는지 확인
        if (auth.currentUser != null) {
            // 이미 로그인된 상태면 홈 화면으로 이동
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            // 로그인되지 않은 상태면 로그인 화면으로 이동
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Step", style = TextStyle(fontSize = 32.sp))
        CircularProgressIndicator()  // 로딩 인디케이터
    }
}
