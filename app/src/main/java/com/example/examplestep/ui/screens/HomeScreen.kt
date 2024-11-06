package com.example.examplestep.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.examplestep.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController:NavController) {
    val stepCount = remember { mutableStateOf(0) }  // 걸음 수를 저장하는 상태 변수
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "걸음 수: ${stepCount.value}",
            style = MaterialTheme.typography.displayLarge,
            color = Color.Black
        )

        // 로그아웃 버튼 추가
        Button(onClick = {
            auth.signOut() // Firebase에서 로그아웃
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInClient.signOut() // GoogleSignInClient 초기화
            navController.navigate("login") // 로그인 화면으로 이동
        }) {
            Text("로그아웃")
        }
    }
}
