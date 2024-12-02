package com.example.examplestep.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examplestep.R
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()


    // 화면이 로드되자마자 Firebase 로그인 상태를 확인
    LaunchedEffect(true) {
        delay(2000) // 2초 대기
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = db.collection("users").document(userId)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }.addOnFailureListener {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // 화면 구성
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 배경 이미지 추가
//        Image(
//            painter = painterResource(id = R.drawable.stepup), // 배경 이미지 리소스 ID
//            contentDescription = "Background",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.FillWidth
//        )

        // 앱 로고와 텍스트
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
               Image(
                    painter = painterResource(id = R.drawable.stepup), // 로고 이미지 리소스 ID
                    contentDescription = "App Logo",
                    modifier = Modifier.size(200.dp)
                )

//                Text(
//                    text = "Step Up",
//                    style = TextStyle(
//                        fontSize = 48.sp,
//                        color = MaterialTheme.colorScheme.primary,
//                        fontFamily = FontFamily.Serif // 원하는 폰트로 변경
//                    )
//                )

            // 애니메이션 효과를 추가한 로딩 인디케이터
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = MaterialTheme.colorScheme.secondary,
                strokeWidth = 6.dp
            )
        }
    }
}