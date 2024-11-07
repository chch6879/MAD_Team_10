package com.example.examplestep.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.examplestep.R
import com.example.examplestep.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.example.examplestep.ui.components.BottomAppBar

@Composable
fun HomeScreen(
    navController:NavController,
    userViewModel: UserViewModel= viewModel()
) {
    var stepCount by remember { mutableStateOf(0) }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    // HomeScreen이 처음 로드될 때 오늘 걸음 수 불러오기
    LaunchedEffect(Unit) {
        userViewModel.getTodaySteps(
            onSuccess = { steps -> stepCount = steps },
            onFailure = { /* 예외 처리 */ }
        )
    }

Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
        BottomAppBar(navController)
    },

) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize().padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Today Step",
                style = MaterialTheme.typography.displayLarge,
                color = Color.Black
            )
            Text(
                text = "$stepCount",
                style = MaterialTheme.typography.displayLarge,
                color = Color.Black
            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        // 걸음 수 증가 버튼
        Button(onClick = {
            stepCount += 1  // 걸음 수 증가
            userViewModel.updateDailySteps(
                stepCount,
                onSuccess = { /* 성공 처리 (필요 시 메시지 표시) */ },
                onFailure = { /* 예외 처리 */ }
            )
        }) {
            Text("걸음 수 증가")
        }

        Spacer(modifier = Modifier.height(32.dp))


        // 로그아웃 버튼 추가
        Button(
            onClick = {
                userViewModel.updateDailySteps(
                    stepCount,
                    onSuccess = {
                        auth.signOut() // Firebase에서 로그아웃
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        googleSignInClient.signOut() // GoogleSignInClient 초기화
                        navController.navigate("login") // 로그인 화면으로 이동
                    },
                    onFailure = { /* 예외 처리 */ }
                )
//            auth.signOut() // Firebase에서 로그아웃
//            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(context.getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//            val googleSignInClient = GoogleSignIn.getClient(context, gso)
//            googleSignInClient.signOut() // GoogleSignInClient 초기화
//            navController.navigate("login") // 로그인 화면으로 이동
            },
            modifier = Modifier.padding(top = 80.dp)
        ) {
            Text("로그아웃")
        }
    }
}

}

