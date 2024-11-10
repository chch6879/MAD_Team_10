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
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()


    LaunchedEffect(true) {
        // 1초 정도 기다린 후 로그인 상태 확인
        delay(1000)

        // 로그인된 사용자가 있는지 확인
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Firebase Authentication에서 로그인된 사용자가 있을 경우
            val userId = currentUser.uid
            val userRef = db.collection("users").document(userId)

            // Firestore에서 해당 사용자가 존재하는지 확인
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Firestore에 사용자가 존재하면 홈 화면으로 이동
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    // Firestore에 사용자가 없다면 로그인 화면으로 이동
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }.addOnFailureListener { e ->
                // Firestore에서 오류 발생 시 로그인 화면으로 이동
                e.printStackTrace()
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        } else {
            // 로그인된 사용자가 없으면 로그인 화면으로 이동
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Step", style = TextStyle(fontSize = 32.sp))
        CircularProgressIndicator()  // 로딩 인디케이터
    }
}
